package com.quotidianity.ui.task_list_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quotidianity.data.Task
import com.quotidianity.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListDetailsViewModel(
    private val repository: TaskRepository,
    private val listId: Int
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getTopLevelTasksForList(listId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(title: String) {
        viewModelScope.launch {
            if (title.isNotBlank()) {
                repository.insertTask(
                    Task(
                        title = title,
                        listId = listId
                    )
                )
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }
}

class TaskListDetailsViewModelFactory(
    private val repository: TaskRepository,
    private val listId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListDetailsViewModel(repository, listId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
