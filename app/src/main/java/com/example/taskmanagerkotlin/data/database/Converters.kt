package com.example.taskmanagerkotlin.data.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun LongToDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun DateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun StringToUUID(value: String?): UUID {
        return UUID.fromString(value)
    }

    @TypeConverter
    fun UUIDTOString(uuid: UUID): String {
        return uuid.toString()
    }
}