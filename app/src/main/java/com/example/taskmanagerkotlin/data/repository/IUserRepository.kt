package com.example.taskmanagerkotlin.data.repository

import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User

interface IUserRepository {
    fun getUsers(): List<User>
    fun getUser(username: String, password: String): User
    fun insertUser(user: User)
    fun deleteUser(user: User)
    fun deleteUserTasks(userId: Long)
    fun getUserTasks(userId: Long): List<Task>
    fun numberOfTask(userId: Long): Int
}