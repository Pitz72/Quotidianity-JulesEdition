package com.quotidianity.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quotidianity.data.TaskRepository
import com.quotidianity.data.TaskList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTaskLists: StateFlow<List<TaskList>> = repository.allTaskLists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            repository.deleteTaskList(taskList)
        }
    }

    fun updateTaskList(taskList: TaskList) {
        viewModelScope.launch {
            repository.updateTaskList(taskList)
        }
    }
}

class HomeViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
