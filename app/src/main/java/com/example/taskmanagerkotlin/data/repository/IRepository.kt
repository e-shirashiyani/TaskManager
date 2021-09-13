package com.example.taskmanagerkotlin.data.repository

import com.example.taskmanagerkotlin.data.model.Task
import java.io.File
import java.util.*

interface IRepository {
    fun getTasks(): List<Task>
    fun searchTasks(searchValue: String, userId: Long): List<Task>
    fun getTask(taskId: UUID): Task
    fun insertTask(task: Task)
    fun insertTasks(tasks: List<Task>)
    fun updateTask(task: Task)
    fun deleteTask(task: Task)
    fun deleteAllTask()
    fun getTodoTask(userId: Long): List<Task>
    fun getDoingTask(userId: Long): List<Task>
    fun getDoneTask(userId: Long): List<Task>
    fun getPhotoFile(task: Task): File
}