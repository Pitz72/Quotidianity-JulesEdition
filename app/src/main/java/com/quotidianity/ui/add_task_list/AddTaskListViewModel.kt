package com.quotidianity.ui.add_task_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quotidianity.data.ListType
import com.quotidianity.data.TaskList
import com.quotidianity.data.TaskRepository
import com.quotidianity.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTaskListUiState(
    val taskList: TaskList? = null,
    val isEditing: Boolean = false,
    val title: String = "",
    val colorHex: String = defaultCategoryColors.values.first()
)

class AddTaskListViewModel(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: Int = savedStateHandle[Screen.AddTaskList.ARG_LIST_ID] ?: -1

    private val _uiState = MutableStateFlow(AddTaskListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (listId != -1) {
            viewModelScope.launch {
                val taskList = repository.getTaskListById(listId).first()
                _uiState.update {
                    it.copy(
                        taskList = taskList,
                        isEditing = true,
                        title = taskList.title,
                        colorHex = taskList.categoryColor
                    )
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onColorChange(newColorHex: String) {
        _uiState.update { it.copy(colorHex = newColorHex) }
    }

    fun saveTaskList() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState.title.isBlank()) return@launch

            if (currentUiState.isEditing) {
                val updatedList = currentUiState.taskList!!.copy(
                    title = currentUiState.title,
                    categoryColor = currentUiState.colorHex
                )
                repository.updateTaskList(updatedList)
            } else {
                val newList = TaskList(
                    title = currentUiState.title,
                    categoryColor = currentUiState.colorHex,
                    type = ListType.SIMPLE // Defaulting to SIMPLE
                )
                repository.insertTaskList(newList)
            }
        }
    }
}
