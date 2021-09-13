package com.example.taskmanagerkotlin.view.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User
import com.example.taskmanagerkotlin.data.repository.IRepository
import com.example.taskmanagerkotlin.data.repository.IUserRepository
import com.example.taskmanagerkotlin.data.repository.TaskDBRepository
import com.example.taskmanagerkotlin.data.repository.UserDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentInsertTaskBinding
import com.example.taskmanagerkotlin.databinding.FragmentTabsBinding
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_Username = "username"
private const val ARG_Password = "password"
private const val BUNDLE_KEY_DATE = "BUNDLE_KEY_DATE"
private const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
private const val FRAGMENT_TAG_DATE_PICKER = "DatePicker"
private const val REQUEST_CODE_DATE_PICKER = 0
private const val FRAGMENT_TAG_TIME_PICKER = "TimePicker"
private const val REQUEST_CODE_TIME_PICKER = 1
private const val REQUEST_CODE_IMAGE_CAPTURE = 2
private const val TAG = "ETF"
private const val AUTHORITY = "org.maktab.taskmanager.fileProvider"

class InsertTaskFragment : DialogFragment() {
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var insertTaskBinding: FragmentInsertTaskBinding
    private lateinit var mDate: String
    private  lateinit var mTime:String
    private var mFlag = false
    private lateinit var mRepository: IRepository
    private lateinit var mTask: Task
    private lateinit var mCalendar: Calendar
    private lateinit var mPhotoFile: File
    private lateinit var mUser: User
    private lateinit var mIUserRepository: IUserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_Username).toString()
            password = it.getString(ARG_Password).toString()
        }
        if (savedInstanceState != null) {
            mDate = savedInstanceState.getString(BUNDLE_KEY_DATE).toString()
            mTime = savedInstanceState.getString(BUNDLE_KEY_TIME).toString()
            mFlag = true
        }

        mRepository = TaskDBRepository.getInstance(activity!!)!!
        mCalendar = Calendar.getInstance()
        createTask()
        mPhotoFile = mRepository.getPhotoFile(mTask)
        mIUserRepository = UserDBRepository.getInstance(activity!!)!!
        mUser = mIUserRepository.getUser(Objects.requireNonNull(username), password)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        insertTaskBinding = DataBindingUtil.inflate<FragmentInsertTaskBinding>(
            inflater,
            R.layout.fragment_insert_task, container, false
        )

        if (mFlag) {
            insertTaskBinding.btnDateInsert.text = mDate
            insertTaskBinding.btnTimeInsert.text = mTime
        }
        listeners()
        return insertTaskBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_KEY_DATE, insertTaskBinding.btnDateInsert.text.toString())
        outState.putString(BUNDLE_KEY_TIME, insertTaskBinding.btnTimeInsert.text.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) return
        when (requestCode) {
            REQUEST_CODE_DATE_PICKER -> {
                val userSelectedDate =
                    data.getSerializableExtra(EXTRA_USER_SELECTED_DATE) as Calendar?
                updateTaskDate(userSelectedDate!!.time)
            }
            REQUEST_CODE_TIME_PICKER -> {
                val userSelectedTime =
                    data.getSerializableExtra(EXTRA_USER_SELECTED_TIME) as Calendar?
                updateTaskTime(userSelectedTime!!.time)
            }
            REQUEST_CODE_IMAGE_CAPTURE -> {
                val photoUri = generateUriForPhotoFile()
                activity!!.revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }
        }
    }

    private fun listeners() {
        insertTaskBinding.btnSaveInsert.setOnClickListener(View.OnClickListener {
            if (validateInput()) {
                sendResult()
                dismiss()
            } else {
                val strId: Int = R.string.toast_insert
                val toast = Toast.makeText(activity, strId, Toast.LENGTH_SHORT)
                toast.show()
            }
        })
        insertTaskBinding.btnCancelInsert.setOnClickListener(View.OnClickListener { dismiss() })
        insertTaskBinding.btnDateInsert.setOnClickListener(View.OnClickListener {
            val datePickerFragment: DatePickerFragment =
                DatePickerFragment.newInstance(mCalendar.time)

            //create parent-child relations between CDF and DPF
            datePickerFragment.setTargetFragment(
                this@InsertTaskFragment,
                REQUEST_CODE_DATE_PICKER
            )
            datePickerFragment.show(
                activity!!.supportFragmentManager,
                FRAGMENT_TAG_DATE_PICKER
            )
        })
        insertTaskBinding.btnTimeInsert.setOnClickListener(View.OnClickListener {
            val timePickerFragment: TimePickerFragment =
                TimePickerFragment.newInstance(mCalendar.time)
            timePickerFragment.setTargetFragment(
                this@InsertTaskFragment,
                REQUEST_CODE_TIME_PICKER
            )
            timePickerFragment.show(
                activity!!.supportFragmentManager,
                FRAGMENT_TAG_TIME_PICKER
            )
        })
        insertTaskBinding.btnPictureInsert.setOnClickListener(View.OnClickListener { takePictureIntent() })
    }

    private fun sendResult() {
        val fragment = targetFragment
        val requestCode = targetRequestCode
        val resultCode = Activity.RESULT_OK
        val intent = Intent()
        updateTask()
        insertTaskToRepository(mTask)
        fragment!!.onActivityResult(requestCode, resultCode, intent)
    }

    private fun takePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (mPhotoFile != null && takePictureIntent
                    .resolveActivity(activity!!.packageManager) != null
            ) {

                // file:///data/data/com.example.ci/files/234234234234.jpg
                val photoUri: Uri = generateUriForPhotoFile()
                grantWriteUriToAllResolvedActivities(takePictureIntent, photoUri)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(
                    takePictureIntent,
                    REQUEST_CODE_IMAGE_CAPTURE
                )
            }
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun grantWriteUriToAllResolvedActivities(takePictureIntent: Intent, photoUri: Uri) {
        val activities = activity!!.packageManager
            .queryIntentActivities(
                takePictureIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        for (activity in activities) {
            getActivity()!!.grantUriPermission(
                activity.activityInfo.packageName,
                photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    private fun generateUriForPhotoFile(): Uri {
        return FileProvider.getUriForFile(
            context!!,
            AUTHORITY,
            mPhotoFile
        )
    }

    private fun validateInput(): Boolean {
        return insertTaskBinding.titleInsert.text != null &&
            insertTaskBinding.descriptionInsert.text != null &&
            insertTaskBinding.btnDateInsert.text.toString() != "Date" &&
                insertTaskBinding.btnTimeInsert.text.toString() != "Time" &&
            (insertTaskBinding.radioBtnTodo.isChecked || insertTaskBinding.radioBtnDoing.isChecked
                    || insertTaskBinding.radioBtnDone.isChecked)
    }

    private fun updateTask() {
        var state = ""
        when {
            insertTaskBinding.radioBtnTodo.isChecked -> state = "Todo"
            insertTaskBinding.radioBtnDoing.isChecked -> state = "Doing"
            insertTaskBinding.radioBtnDone.isChecked -> state = "Done"
        }

        mTask.mTitle=insertTaskBinding.titleInsert.text.toString()
        mTask.mDescription=insertTaskBinding.descriptionInsert.text.toString()
        mTask.mDate=mCalendar.time
        mTask.mState=state
        mTask.userIdFk=mUser.primaryId
    }

    private fun insertTaskToRepository(task: Task) {
        mRepository.insertTask(task)
    }

    private fun createTask() {
        mTask = Task("", "", Date(), "")
    }

    private fun updatePhotoView() {
        /*if (mPhotoFile == null || !mPhotoFile.exists()) return


        //this has a better memory management.
        val bitmap: Bitmap = PictureUtils.getBitmap(
            mPhotoFile.absolutePath,
            activity
        )
        insertTaskBinding.btnPictureInsert.setImageBitmap(bitmap)*/
    }

    private fun updateTaskDate(userSelectedDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = userSelectedDate
        val year = calendar[Calendar.YEAR]
        val monthOfYear = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = monthOfYear
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val dateFormat = getDateFormat()
        insertTaskBinding.btnDateInsert.text = dateFormat.format(userSelectedDate)
    }

    private fun updateTaskTime(userSelectedTime: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = userSelectedTime
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        mCalendar[Calendar.HOUR_OF_DAY] = hour
        mCalendar[Calendar.MINUTE] = minute
        val timeFormat = getTimeFormat()
        insertTaskBinding.btnTimeInsert.text = timeFormat.format(userSelectedTime)
    }


    private fun getDateFormat(): DateFormat {
        //"yyyy/MM/dd"
        return SimpleDateFormat("MMM dd,yyyy")
    }

    private fun getTimeFormat(): DateFormat {
        //"HH:mm:ss"
        return SimpleDateFormat("h:mm a")
    }

    companion object {
        @JvmStatic
        fun newInstance(username: String, password: String) =
            InsertTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_Username, username)
                    putString(ARG_Password, password)
                }
            }
    }
}