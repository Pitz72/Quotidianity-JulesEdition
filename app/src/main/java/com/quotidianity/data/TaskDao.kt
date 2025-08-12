package com.quotidianity.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // --- TaskList Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskList)

    @Update
    suspend fun updateTaskList(taskList: TaskList)

    @Delete
    suspend fun deleteTaskList(taskList: TaskList)

    @Query("SELECT * FROM task_lists ORDER BY id DESC")
    fun getAllTaskLists(): Flow<List<TaskList>>

    @Query("SELECT * FROM task_lists WHERE id = :id")
    fun getTaskListById(id: Int): Flow<TaskList>

    // --- Task Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE list_id = :listId AND parent_id IS NULL ORDER BY id ASC")
    fun getTopLevelTasksForList(listId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE parent_id = :parentId ORDER BY id ASC")
    fun getSubTasks(parentId: Int): Flow<List<Task>>
}
