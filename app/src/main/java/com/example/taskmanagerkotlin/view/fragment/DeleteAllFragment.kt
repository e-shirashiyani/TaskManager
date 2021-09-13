package com.example.taskmanagerkotlin.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.repository.IRepository
import com.example.taskmanagerkotlin.data.repository.TaskDBRepository

class DeleteAllFragment : DialogFragment() {

    private lateinit var mRepository: IRepository
    private lateinit var mTasks: List<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepository = TaskDBRepository.getInstance(activity!!)!!
        mTasks = mRepository.getTasks()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.fragment_delete_all, null)
        val builder = AlertDialog.Builder(activity)
        if (mTasks.isNotEmpty()) {
            builder.setTitle(R.string.delete_all_title)
            builder.setIcon(R.drawable.ic_high_importance)
            builder.setView(view)
            builder.setPositiveButton(R.string.yes,
                DialogInterface.OnClickListener { dialog, which -> mRepository.deleteAllTask() })
                .setNegativeButton(R.string.no, null)
        } else {
            builder.setTitle(R.string.no_tasks)
            builder.setNegativeButton(R.string.exit, null)
        }
        return builder.create()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DeleteAllFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}