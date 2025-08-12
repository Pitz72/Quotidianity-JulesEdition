package com.quotidianity.ui.add_task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quotidianity.data.ListType
import com.quotidianity.data.TaskList
import com.quotidianity.data.TaskRepository
import kotlinx.coroutines.launch

class AddTaskListViewModel(private val repository: TaskRepository) : ViewModel() {

    fun addTaskList(title: String, colorHex: String) {
        viewModelScope.launch {
            if (title.isNotBlank()) {
                repository.insertTaskList(
                    TaskList(
                        title = title,
                        categoryColor = colorHex,
                        type = ListType.SIMPLE // Defaulting to SIMPLE for now
                    )
                )
            }
        }
    }
}

class AddTaskListViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTaskListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
