package com.quotidianity.ui.task_list_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quotidianity.QuotidianityApplication
import com.quotidianity.R
import com.quotidianity.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListDetailsScreen(
    listId: Int,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val viewModel: TaskListDetailsViewModel = viewModel(
        factory = TaskListDetailsViewModelFactory(
            (LocalContext.current.applicationContext as QuotidianityApplication).repository,
            listId
        )
    )
    val tasks by viewModel.tasks.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    if (taskToEdit != null) {
        EditTaskDialog(
            task = taskToEdit!!,
            onDismiss = { taskToEdit = null },
            onConfirm = { updatedTitle ->
                viewModel.updateTaskTitle(taskToEdit!!, updatedTitle)
                taskToEdit = null
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Task List") }, // This should probably show the list title
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_button_description))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onCheckChanged = { viewModel.toggleTaskCompletion(task) },
                        onDeleteClick = { viewModel.deleteTask(task) },
                        onTextClick = { taskToEdit = task }
                    )
                }
            }
            // Input for new task
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("New Task") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.addTask(newTaskTitle)
                        newTaskTitle = ""
                    },
                    enabled = newTaskTitle.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckChanged
        )
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
                .clickable(onClick = onTextClick)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var updatedTitle by remember { mutableStateOf(task.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            OutlinedTextField(
                value = updatedTitle,
                onValueChange = { updatedTitle = it },
                label = { Text("Task Title") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(updatedTitle) },
                enabled = updatedTitle.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
