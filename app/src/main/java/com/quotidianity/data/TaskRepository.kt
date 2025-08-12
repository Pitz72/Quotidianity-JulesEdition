package com.quotidianity.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTaskLists: Flow<List<TaskList>> = taskDao.getAllTaskLists()

    fun getTopLevelTasksForList(listId: Int): Flow<List<Task>> {
        return taskDao.getTopLevelTasksForList(listId)
    }

    fun getSubTasks(parentId: Int): Flow<List<Task>> {
        return taskDao.getSubTasks(parentId)
    }

    fun getTaskListById(id: Int): Flow<TaskList> {
        return taskDao.getTaskListById(id)
    }

    suspend fun insertTaskList(taskList: TaskList) {
        taskDao.insertTaskList(taskList)
    }

    suspend fun updateTaskList(taskList: TaskList) {
        taskDao.updateTaskList(taskList)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTaskList(taskList: TaskList) {
        taskDao.deleteTaskList(taskList)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}
