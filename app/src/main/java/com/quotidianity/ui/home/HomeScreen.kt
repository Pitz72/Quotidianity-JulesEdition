package com.quotidianity.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quotidianity.QuotidianityApplication
import com.quotidianity.R
import com.quotidianity.data.TaskList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit,
    onTaskListClick: (Int) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory((LocalContext.current.applicationContext as QuotidianityApplication).repository)
    )
    val taskLists by viewModel.allTaskLists.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.home_screen_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_list_fab_description))
            }
        }
    ) { paddingValues ->
        if (taskLists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.no_lists_placeholder))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(taskLists) { taskList ->
                    TaskListCard(taskList = taskList, onClick = { onTaskListClick(taskList.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListCard(
    taskList: TaskList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color = Color(android.graphics.Color.parseColor(taskList.categoryColor)))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = taskList.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
