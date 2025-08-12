package com.quotidianity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [TaskList::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(AppDatabase.Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Converters {
        @TypeConverter
        fun fromListType(value: ListType): String = value.name

        @TypeConverter
        fun toListType(value: String): ListType = ListType.valueOf(value)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quotidianity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
