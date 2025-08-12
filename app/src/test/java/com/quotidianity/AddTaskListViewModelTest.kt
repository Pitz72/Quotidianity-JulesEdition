package com.quotidianity

import com.quotidianity.data.ListType
import com.quotidianity.data.Task
import com.quotidianity.data.TaskList
import com.quotidianity.data.TaskRepository
import com.quotidianity.data.TaskDao
import com.quotidianity.ui.add_task_list.AddTaskListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTaskListViewModelTest {

    private lateinit var viewModel: AddTaskListViewModel
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
        viewModel = AddTaskListViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addTaskList_whenTitleIsNotBlank_insertsList() = runTest {
        // Given
        val title = "New Test List"
        val color = "#FF0000"

        // When
        viewModel.addTaskList(title, color)

        // Then
        val insertedList = fakeRepository.insertedTaskLists.first()
        assertEquals(1, fakeRepository.insertedTaskLists.size)
        assertEquals(title, insertedList.title)
        assertEquals(color, insertedList.categoryColor)
        assertEquals(ListType.SIMPLE, insertedList.type)
    }

    @Test
    fun addTaskList_whenTitleIsBlank_doesNotInsertList() = runTest {
        // Given
        val title = ""
        val color = "#FF0000"

        // When
        viewModel.addTaskList(title, color)

        // Then
        assertEquals(0, fakeRepository.insertedTaskLists.size)
    }
}

// A fake repository implementation for testing purposes
class FakeTaskRepository : TaskRepository(FakeTaskDao()) {
    val insertedTaskLists = mutableListOf<TaskList>()
    val insertedTasks = mutableListOf<Task>()

    override suspend fun insertTaskList(taskList: TaskList) {
        insertedTaskLists.add(taskList)
    }

    override suspend fun insertTask(task: Task) {
        insertedTasks.add(task)
    }
}

// A fake DAO that does nothing, as the repository overrides the methods
class FakeTaskDao : TaskDao {
    override suspend fun insertTaskList(taskList: TaskList) {}
    override suspend fun updateTaskList(taskList: TaskList) {}
    override suspend fun deleteTaskList(taskList: TaskList) {}
    override fun getAllTaskLists(): Flow<List<TaskList>> = flowOf(emptyList())
    override suspend fun insertTask(task: Task) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {}
    override fun getTopLevelTasksForList(listId: Int): Flow<List<Task>> = flowOf(emptyList())
    override fun getSubTasks(parentId: Int): Flow<List<Task>> = flowOf(emptyList())
}
