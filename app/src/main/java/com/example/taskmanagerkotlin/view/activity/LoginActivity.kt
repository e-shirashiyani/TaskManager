package com.example.taskmanagerkotlin.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.view.fragment.DatePickerFragment.Companion.newInstance
import com.example.taskmanagerkotlin.view.fragment.DeleteAllFragment.Companion.newInstance
import com.example.taskmanagerkotlin.view.fragment.EditTaskFragment.Companion.newInstance
import com.example.taskmanagerkotlin.view.fragment.LoginFragment

class LoginActivity : SingleFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }
    override fun CreateFragment(): Fragment {
return LoginFragment.newInstance() }
}