package com.example.taskmanagerkotlin.view.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.taskmanagerkotlin.view.fragment.DeleteAllFragment.Companion.newInstance
import com.example.taskmanagerkotlin.view.fragment.TaskListFragment

class TaskListActivity : SingleFragmentActivity() {

    companion object {
        const val EXTRA_USERNAME =
            "com.example.taskmanagerkotlin.view.activity.extra_username"
        const val EXTRA_PASSWORD =
            "com.example.taskmanagerkotlin.view.activity.extra_password"

        fun newIntent(context: Context?, username: String, password: String): Intent {

            val intent = Intent(context, TaskListActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, username)
            intent.putExtra(EXTRA_PASSWORD, password)
            return intent
        }


    }

    override fun CreateFragment(): Fragment {
        return TaskListFragment.newInstance(
            intent.getStringExtra(EXTRA_USERNAME).toString(),
            intent.getStringExtra(EXTRA_PASSWORD).toString()
        )
    }
}