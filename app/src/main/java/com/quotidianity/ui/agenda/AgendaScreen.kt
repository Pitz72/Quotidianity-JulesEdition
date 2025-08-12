package com.quotidianity.ui.agenda

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quotidianity.auth.GoogleAuthUiClient
import com.quotidianity.data.calendar.CalendarRepository
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendarRepository by remember {
        lazy {
            CalendarRepository(
                context = context,
                googleAuthUiClient = GoogleAuthUiClient.create(context)
            )
        }
    }

    val viewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(calendarRepository)
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.loadEvents()
    }

    val events by viewModel.events.collectAsState()

    if (events.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No upcoming events found in your calendar.")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(events) { event ->
                EventItem(
                    summary = event.summary ?: "No Title",
                    startTime = event.start?.dateTime?.toString() ?: "No Time"
                )
            }
        }
    }
}

@Composable
fun EventItem(
    summary: String,
    startTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = summary, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatDateTime(startTime), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val date = com.google.api.client.util.DateTime(dateTimeString)
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        sdf.format(date.value)
    } catch (e: Exception) {
        dateTimeString
    }
}
