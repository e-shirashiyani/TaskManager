package com.example.taskmanagerkotlin.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user")
class User(
    @ColumnInfo(name = "username")
    var mUsername: String?,
    @ColumnInfo(name = "password")
    var mPassword: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    var primaryId: Long = 0

    @ColumnInfo(name = "date")
    var mDate: Date? = null

    init {
        this.mDate = Date()
    }

}