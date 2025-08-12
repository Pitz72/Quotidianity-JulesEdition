package com.quotidianity

import androidx.lifecycle.SavedStateHandle
import com.quotidianity.data.ListType
import com.quotidianity.data.Task
import com.quotidianity.data.TaskList
import com.quotidianity.data.TaskDao
import com.quotidianity.data.TaskRepository
import com.quotidianity.ui.add_task_list.AddTaskListViewModel
import com.quotidianity.ui.navigation.Screen
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
import org.junit.Assert.assertTrue
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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveTaskList with new list inserts into repository`() = runTest {
        // Given a ViewModel for a new list (no listId)
        val savedStateHandle = SavedStateHandle()
        viewModel = AddTaskListViewModel(fakeRepository, savedStateHandle)

        val title = "New Test List"
        val color = "#FF0000"

        // When
        viewModel.onTitleChange(title)
        viewModel.onColorChange(color)
        viewModel.saveTaskList()

        // Then
        assertEquals(1, fakeRepository.insertedTaskLists.size)
        assertEquals(0, fakeRepository.updatedTaskLists.size)
        val insertedList = fakeRepository.insertedTaskLists.first()
        assertEquals(title, insertedList.title)
        assertEquals(color, insertedList.categoryColor)
    }

    @Test
    fun `saveTaskList with existing list updates repository`() = runTest {
        // Given an existing task list in the repository
        val existingList = TaskList(id = 1, title = "Original Title", categoryColor = "#0000FF", type = ListType.SIMPLE)
        fakeRepository.addInitialList(existingList)

        // And a ViewModel for that existing list
        val savedStateHandle = SavedStateHandle(mapOf(Screen.AddTaskList.ARG_LIST_ID to 1))
        viewModel = AddTaskListViewModel(fakeRepository, savedStateHandle)

        // Wait for the ViewModel to load the existing list
        testDispatcher.scheduler.advanceUntilIdle()

        val newTitle = "Updated Title"
        val newColor = "#00FF00"

        // When
        viewModel.onTitleChange(newTitle)
        viewModel.onColorChange(newColor)
        viewModel.saveTaskList()

        // Then
        assertEquals(0, fakeRepository.insertedTaskLists.size)
        assertEquals(1, fakeRepository.updatedTaskLists.size)
        val updatedList = fakeRepository.updatedTaskLists.first()
        assertEquals(newTitle, updatedList.title)
        assertEquals(newColor, updatedList.categoryColor)
        assertEquals(existingList.id, updatedList.id)
    }

    @Test
    fun `saveTaskList with blank title does nothing`() = runTest {
        // Given a ViewModel for a new list
        val savedStateHandle = SavedStateHandle()
        viewModel = AddTaskListViewModel(fakeRepository, savedStateHandle)

        // When
        viewModel.onTitleChange("") // Blank title
        viewModel.saveTaskList()

        // Then
        assertTrue(fakeRepository.insertedTaskLists.isEmpty())
        assertTrue(fakeRepository.updatedTaskLists.isEmpty())
    }
}

// A fake repository implementation for testing purposes
class FakeTaskRepository : TaskRepository(FakeTaskDao()) {
    val insertedTaskLists = mutableListOf<TaskList>()
    val updatedTaskLists = mutableListOf<TaskList>()
    private val initialLists = mutableListOf<TaskList>()

    fun addInitialList(taskList: TaskList) {
        initialLists.add(taskList)
    }

    override suspend fun insertTaskList(taskList: TaskList) {
        insertedTaskLists.add(taskList)
    }

    override suspend fun updateTaskList(taskList: TaskList) {
        updatedTaskLists.add(taskList)
    }

    override fun getTaskListById(id: Int): Flow<TaskList> {
        return flowOf(initialLists.first { it.id == id })
    }
}

// A fake DAO that does nothing, as the repository overrides the methods
class FakeTaskDao : TaskDao {
    override suspend fun insertTaskList(taskList: TaskList) {}
    override suspend fun updateTaskList(taskList: TaskList) {}
    override suspend fun deleteTaskList(taskList: TaskList) {}
    override fun getAllTaskLists(): Flow<List<TaskList>> = flowOf(emptyList())
    override fun getTaskListById(id: Int): Flow<TaskList> = flowOf()
    override suspend fun insertTask(task: Task) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {}
    override fun getTopLevelTasksForList(listId: Int): Flow<List<Task>> = flowOf(emptyList())
    override fun getSubTasks(parentId: Int): Flow<List<Task>> = flowOf(emptyList())
}
