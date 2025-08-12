package com.quotidianity.ui.add_task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quotidianity.R

val defaultCategoryColors = mapOf(
    "Casa" to "#42A5F5",    // Blue
    "Lavoro" to "#66BB6A",  // Green
    "Salute" to "#EF5350",  // Red
    "Personale" to "#AB47BC",// Purple
    "Finanze" to "#FFEE58" // Yellow
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTaskListViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit List" else stringResource(id = R.string.add_list_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_button_description))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text(stringResource(id = R.string.list_title_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(id = R.string.choose_color_label), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            ColorSelector(
                selectedColorHex = uiState.colorHex,
                onColorSelected = { viewModel.onColorChange(it) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.saveTaskList()
                    onNavigateBack()
                },
                enabled = uiState.title.isNotBlank()
            ) {
                Text(stringResource(id = R.string.save_list_button))
            }
        }
    }
}

@Composable
fun ColorSelector(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(defaultCategoryColors.values.toList()) { colorHex ->
            val color = Color(android.graphics.Color.parseColor(colorHex))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(colorHex) }
                    .border(
                        width = 3.dp,
                        color = if (selectedColorHex == colorHex) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
            )
        }
    }
}
