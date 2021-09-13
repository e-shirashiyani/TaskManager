package com.example.taskmanagerkotlin.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerkotlin.R
import java.util.*


private const val ARG_DATE_PICKER= "datePicker"
const val EXTRA_USER_SELECTED_DATE="com.example.criminalintent.userSelectedDate"

class DatePickerFragment : DialogFragment() {
    private lateinit var taskDate: Date
    private lateinit var mCalender:Calendar
    private lateinit var mDatePicker:DatePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           taskDate=it.getSerializable(ARG_DATE_PICKER) as Date
        }
        mCalender= Calendar.getInstance()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
   val inflater=LayoutInflater.from(activity)
        val view=inflater.inflate(R.layout.fragment_date_picker,null)
    findViews(view)
        initViews()
        val builder: AlertDialog.Builder= AlertDialog.Builder(activity)
            .setTitle(" Date Of Task")
            .setIcon(R.drawable.ic_calendar)
            .setView(view)
            .setPositiveButton(android.R.string.ok,DialogInterface.OnClickListener{dialof,which->
                extractDateFromDatePicker()
                sendResult(mCalender)

            })
            .setNegativeButton(android.R.string.cancel,null)
        return builder.create()
    }

    private fun sendResult(userSelectedDate: Calendar) {
        val fragment=targetFragment
        val requestCode =targetRequestCode
        val resultCode= Activity.RESULT_OK
        val intent=Intent()
        intent.putExtra(EXTRA_USER_SELECTED_DATE,userSelectedDate)
        fragment!!.onActivityResult(requestCode,resultCode,intent)
    }

    private fun extractDateFromDatePicker() {
        val year: Int = mDatePicker.year
        val month: Int = mDatePicker.month
        val dayOfMonth: Int = mDatePicker.dayOfMonth
        mCalender.set(Calendar.YEAR, year)
        mCalender.set(Calendar.MONTH, month)
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    private fun findViews(view: View?) {
        if (view != null) {
            mDatePicker = view.findViewById(R.id.date_picker_task)
        }
    }

    private fun initViews() {
        initDatePicker()
    }

    private fun initDatePicker() {
        mCalender.time = taskDate
        val year: Int = mCalender.get(Calendar.YEAR)
        val monthOfYear: Int = mCalender.get(Calendar.MONTH)
        val dayOfMonth: Int = mCalender.get(Calendar.DAY_OF_MONTH)
        mDatePicker.init(year, monthOfYear, dayOfMonth, null)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: Date) =
            DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DATE_PICKER,param1)

                }
            }


    }
}