package com.example.taskmanagerkotlin.data.repository

import android.content.Context
import androidx.room.Room
import com.example.taskmanagerkotlin.data.database.TaskDatabase
import com.example.taskmanagerkotlin.data.database.TaskDatabaseDAO
import com.example.taskmanagerkotlin.data.model.Task
import java.io.File
import java.util.*

class TaskDBRepository private constructor(context: Context) : IRepository {
    private val mTaskDAO: TaskDatabaseDAO
    private val mContext: Context
    private val mTasks: List<Task>? = null

    override fun getTasks(): List<Task> {
        return mTaskDAO.getTasks()
    }

    override fun searchTasks(searchValue: String, userId: Long): List<Task> {
        return mTaskDAO.searchTasks(searchValue, userId)
    }

    override fun getTask(taskId: UUID): Task {
        return mTaskDAO.getTask(taskId)
    }

    override fun insertTask(task: Task) {
        mTaskDAO.insertTask(task)
    }

    override fun insertTasks(tasks: List<Task>) {
        mTaskDAO.insertTasks(tasks)
    }

    override fun updateTask(task: Task) {
        mTaskDAO.updateTask(task)
    }

    override fun deleteTask(task: Task) {
        mTaskDAO.deleteTask(task)
    }

    override fun deleteAllTask() {
        mTaskDAO.deleteAllTask()
    }

    override fun getTodoTask(userId: Long): List<Task> {
        return mTaskDAO.getTodoTask(userId)
    }

    override fun getDoingTask(userId: Long): List<Task> {
        return mTaskDAO.getDoingTask(userId)
    }

    override fun getDoneTask(userId: Long): List<Task> {
        return mTaskDAO.getDoneTask(userId)
    }

    override fun getPhotoFile(task: Task): File {
        // /data/data/com.example.criminalintent/files/
        val filesDir = mContext.filesDir

        // /data/data/com.example.criminalintent/files/IMG_ktui4u544nmkfuy48485.jpg
        return File(filesDir, task.getPhotoFileName())
    }

    companion object {
        private var sInstance: TaskDBRepository? = null
        fun getInstance(context: Context): TaskDBRepository? {
            if (sInstance == null) sInstance = TaskDBRepository(context)
            return sInstance
        }
    }

    init {
        mContext = context.applicationContext
        val taskDatabase = Room.databaseBuilder(
            mContext,
            TaskDatabase::class.java,
            "task.db"
        )
            .allowMainThreadQueries()
            .build()

        mTaskDAO = taskDatabase.getTaskDatabaseDAO()
    }
}