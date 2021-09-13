package com.example.taskmanagerkotlin.data.database

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User

@Database(entities = [Task::class, User::class], version = 1)
@TypeConverters(com.example.taskmanagerkotlin.data.database.Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDatabaseDAO(): TaskDatabaseDAO
}