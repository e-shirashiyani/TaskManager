package com.example.taskmanagerkotlin.data.repository

import android.content.Context
import androidx.room.Room
import com.example.taskmanagerkotlin.data.database.TaskDatabase
import com.example.taskmanagerkotlin.data.database.TaskDatabaseDAO
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User

class UserDBRepository private constructor(context: Context) : IUserRepository {
    private val mTaskDAO: TaskDatabaseDAO
    private val mContext: Context
    override fun getUsers(): List<User> {
        return mTaskDAO.getUsers()
    }

    override fun getUser(username: String, password: String): User {
        return mTaskDAO.getUser(username, password)
    }

    override fun insertUser(user: User) {
        mTaskDAO.insertUser(user)
    }

    override fun deleteUser(user: User) {
        mTaskDAO.deleteUser(user)
    }

    override fun deleteUserTasks(userId: Long) {
        mTaskDAO.deleteUserTasks(userId)
    }

    override fun getUserTasks(userId: Long): List<Task> {
        return mTaskDAO.getUserTasks(userId)
    }

    override fun numberOfTask(userId: Long): Int {
        return mTaskDAO.numberOfTask(userId)
    }

    companion object {
        private var sInstance: UserDBRepository? = null
        fun getInstance(context: Context): UserDBRepository? {
            if (sInstance == null) sInstance = UserDBRepository(context)
            return sInstance
        }
    }

    init {
        mContext = context.applicationContext
        val taskDatabase: TaskDatabase = Room.databaseBuilder(
            mContext,
            TaskDatabase::class.java,
            "task.db"
        )
            .allowMainThreadQueries()
            .build()
        mTaskDAO = taskDatabase.getTaskDatabaseDAO()
    }
}