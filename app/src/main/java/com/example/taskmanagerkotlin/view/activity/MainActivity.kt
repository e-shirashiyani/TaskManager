package com.example.taskmanagerkotlin.view.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var mMainActivityBinding: ActivityMainBinding? = null
    private val mContext: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val handler = Handler()
        handler.postDelayed({

                val intent = Intent(mContext, LoginActivity::class.java)
                startActivity(intent)
                finish()

        }, 4000)

    }



}