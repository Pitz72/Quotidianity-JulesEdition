package com.quotidianity

import android.app.Application
import com.quotidianity.data.AppDatabase
import com.quotidianity.data.TaskRepository

class QuotidianityApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}
