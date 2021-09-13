package com.example.taskmanagerkotlin.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.taskmanagerkotlin.R

abstract class SingleFragmentActivity : AppCompatActivity() {
    abstract fun CreateFragment(): Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        val fragmentManager = supportFragmentManager


        val fragment = fragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, CreateFragment())
                .commit()
        }
    }
}