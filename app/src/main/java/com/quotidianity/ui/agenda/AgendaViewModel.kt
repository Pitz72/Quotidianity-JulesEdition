package com.quotidianity.ui.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.api.services.calendar.model.Event
import com.quotidianity.data.calendar.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar

class AgendaViewModel(private val calendarRepository: CalendarRepository) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    fun loadEvents() {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val to = Calendar.getInstance()
            to.add(Calendar.DAY_OF_YEAR, 30) // Fetch for the next 30 days

            val eventList = calendarRepository.getEvents(now.time, to.time)
            _events.update { eventList }
        }
    }
}

class AgendaViewModelFactory(private val calendarRepository: CalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(calendarRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
