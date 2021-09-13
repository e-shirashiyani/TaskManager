package com.example.taskmanagerkotlin.data.database

import androidx.room.*
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User
import java.util.*
@Dao
interface TaskDatabaseDAO {
    @Update
    fun updateTask(task: Task)

    @Insert
    fun insertTask(task: Task)

    @Insert
    fun insertTasks(tasks: List<Task>)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM task")
    fun deleteAllTask()

    @Query("SELECT * FROM task")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM task WHERE state ='Todo' AND user_id_fk=:userId")
    fun getTodoTask(userId: Long): List<Task>

    @Query("SELECT * FROM task WHERE state ='Doing' AND user_id_fk=:userId")
    fun getDoingTask(userId: Long): List<Task>

    @Query("SELECT * FROM task WHERE state ='Done' AND user_id_fk=:userId")
    fun getDoneTask(userId: Long): List<Task>

    @Query("SELECT * FROM task WHERE uuid =:inputUUID")
    fun getTask(inputUUID: UUID?): Task

    @Query("SELECT * FROM task WHERE user_id_fk=:userId AND title LIKE :searchValue OR description LIKE :searchValue OR date LIKE :searchValue")
    fun searchTasks(searchValue: String, userId: Long): List<Task>

    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM task WHERE user_id_fk=:userId")
    fun deleteUserTasks(userId: Long)

    @Query("SELECT * FROM task WHERE user_id_fk=:userId")
    fun getUserTasks(userId: Long): List<Task>

    @Query("SELECT * FROM user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM user WHERE  username=:name AND password=:pass")
    fun getUser(name: String, pass: String): User

    @Query("SELECT COUNT(*) FROM task WHERE user_id_fk=:userId GROUP BY user_id_fk")
    fun numberOfTask(userId: Long): Int

}