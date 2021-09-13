package com.example.taskmanagerkotlin.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerkotlin.R
import java.time.LocalDateTime
import java.util.*


private const val ARG_TIME_PICKER = "timePicker"
const val EXTRA_USER_SELECTED_TIME = "org.maktab.taskmanager.userSelectedTime"

class TimePickerFragment : DialogFragment() {
    private lateinit var taskTime: Date
    private lateinit var mCalendar: Calendar
    private lateinit var mTimePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskTime = it.getSerializable(ARG_TIME_PICKER) as Date
        }

        mCalendar = Calendar.getInstance()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.fragment_time_picker, null)
        findViews(view)
        initViews()
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                .setTitle(R.string.time_picker_title)
                .setIcon(R.drawable.ic_clock)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            extractTimeFromTimePicker()
                            sendResult(mCalendar)
                        })
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }

    private fun findViews(view: View?) {
        if (view != null) {
            mTimePicker = view.findViewById(R.id.time_picker_task)
        }
    }

    private fun initViews() {
        initTimePicker()
    }

    private fun initTimePicker() {
        // i have a date and i want to set it in date picker.
        mCalendar.time = taskTime
        val hour = mCalendar[Calendar.HOUR]
        val minute = mCalendar[Calendar.MINUTE]
        mTimePicker.hour = hour
        mTimePicker.minute = minute
    }

    private fun extractTimeFromTimePicker() {
        val now = LocalDateTime.now()
        val hour: Int = mTimePicker.hour
        val minute: Int = mTimePicker.minute
        val second = now.second
        mCalendar[Calendar.HOUR_OF_DAY] = hour
        mCalendar[Calendar.MINUTE] = minute
        mCalendar[Calendar.SECOND] = second
    }

    private fun sendResult(userSelectedDate: Calendar) {
        val fragment = targetFragment
        val requestCode = targetRequestCode
        val resultCode = Activity.RESULT_OK
        val intent = Intent()
        intent.putExtra(EXTRA_USER_SELECTED_TIME, userSelectedDate)
        fragment!!.onActivityResult(requestCode, resultCode, intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(taskTime: Date) =
                TimePickerFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_TIME_PICKER, taskTime)
                    }
                }
    }
}