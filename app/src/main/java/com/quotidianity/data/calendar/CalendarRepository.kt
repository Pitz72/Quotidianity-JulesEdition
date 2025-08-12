package com.quotidianity.data.calendar

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.quotidianity.auth.GoogleAuthUiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class CalendarRepository(
    private val context: Context,
    private val googleAuthUiClient: GoogleAuthUiClient
) {
    private fun getCalendarService(): Calendar? {
        val account = googleAuthUiClient.getSignedInUser() ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(CalendarScopes.CALENDAR)
        ).setSelectedAccountName(account.email)

        return Calendar.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Quotidianity")
            .build()
    }

    suspend fun createEvent(
        title: String,
        description: String?,
        startTime: Date,
        endTime: Date
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val service = getCalendarService() ?: return@withContext false
                val event = Event().apply {
                    summary = title
                    this.description = description
                    start = EventDateTime().setDateTime(com.google.api.client.util.DateTime(startTime))
                    end = EventDateTime().setDateTime(com.google.api.client.util.DateTime(endTime))
                }

                service.events().insert("primary", event).execute()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getEvents(from: Date, to: Date): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val service = getCalendarService() ?: return@withContext emptyList()
                val events = service.events().list("primary")
                    .setDateTimeMin(com.google.api.client.util.DateTime(from))
                    .setDateTimeMax(com.google.api.client.util.DateTime(to))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                events.items ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
