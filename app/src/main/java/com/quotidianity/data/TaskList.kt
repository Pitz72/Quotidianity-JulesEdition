package com.quotidianity.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ListType {
    SIMPLE,
    PROJECT
}

@Entity(tableName = "task_lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val categoryColor: String, // Storing hex color code as a String
    val type: ListType = ListType.SIMPLE
)
