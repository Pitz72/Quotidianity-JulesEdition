package com.quotidianity.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskList::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("list_id")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "list_id")
    val listId: Int,
    @ColumnInfo(name = "parent_id")
    val parentId: Int? = null
)
