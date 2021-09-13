package com.example.taskmanagerkotlin.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task")
class Task() {
    constructor(title: String, description: String, date: Date, state: String) : this() {
        mId = UUID.randomUUID()
        mTitle = title
        mDescription = description
        mDate = date
        mState = state
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    var primaryId: Long = 0

    @ColumnInfo(name = "uuid")
    var mId: UUID? = null

    @ColumnInfo(name = "title")
    var mTitle: String? = null

    @ColumnInfo(name = "description")
    var mDescription: String? = null

    @ColumnInfo(name = "date")
    var mDate: Date? = null

    @ColumnInfo(name = "state")
    var mState: String? = null

    @ColumnInfo(name = "user_id_fk")
    var userIdFk: Long = 0

    fun getPhotoFileName(): String? {
        return "IMG_" + mId.toString() + ".jpg"
    }
}