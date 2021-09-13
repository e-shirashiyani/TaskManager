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
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.repository.IRepository
import com.example.taskmanagerkotlin.data.repository.TaskDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentEditTaskBinding

import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val FRAGMENT_TAG_DATE_PICKER = "DatePicker"
private const val REQUEST_CODE_DATE_PICKER = 0
private const val FRAGMENT_TAG_TIME_PICKER = "TimePicker"
private const val REQUEST_CODE_TIME_PICKER = 1
private const val REQUEST_CODE_IMAGE_CAPTURE = 2
private const val TAG = "ETF"
private const val AUTHORITY = "org.maktab.taskmanager.fileProvider"
private const val BUNDLE_KEY_DATE = "BUNDLE_KEY_DATE"
private const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
const val ARGUMENT_TASK_ID = "Bundle_key_TaskId"
const val ARGUMENT_SHARE_FEATURE = "argument_share_feature"

class EditTaskFragment : DialogFragment() {
    private lateinit var taskId: UUID
    private var sharedFeature: Boolean? = null
    private lateinit var mRepository: IRepository
    private lateinit var mTask: Task
    private lateinit var mCalendar: Calendar
    private lateinit var mPhotoFile: File
    private lateinit var fragmentEditTaskBinding: FragmentEditTaskBinding
    private val mFlag = false
    private val mDate: String? = null
    private var mTime: String? = null
    private var mState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getSerializable(ARGUMENT_TASK_ID) as UUID
            sharedFeature = it.getBoolean(ARGUMENT_SHARE_FEATURE)
        }

        mRepository = TaskDBRepository.getInstance(activity!!)!!
        mTask = mRepository.getTask(taskId)
        mCalendar = Calendar.getInstance()
        mPhotoFile = mRepository.getPhotoFile(mTask)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentEditTaskBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_task,
            container, false
        )

        if (mFlag) {
            fragmentEditTaskBinding.btnDateEdit.text = mDate
            fragmentEditTaskBinding.btnTimeEdit.text = mTime
        }
        if (!sharedFeature!!)
            fragmentEditTaskBinding.share.visibility = View.GONE
        setData(mTask)
        listeners()
        updatePhotoView()
        return fragmentEditTaskBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_KEY_DATE, fragmentEditTaskBinding.btnDateEdit.text.toString())
        outState.putString(BUNDLE_KEY_TIME, fragmentEditTaskBinding.btnTimeEdit.text.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) return
        if (requestCode == REQUEST_CODE_DATE_PICKER) {
            val userSelectedDate =
                data.getSerializableExtra(EXTRA_USER_SELECTED_DATE) as Calendar?
            updateTaskDate(userSelectedDate!!.time)
        } else if (requestCode == REQUEST_CODE_TIME_PICKER) {
            val userSelectedTime =
                data.getSerializableExtra(EXTRA_USER_SELECTED_TIME) as Calendar?
            updateTaskTime(userSelectedTime!!.time)
        } else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
            val photoUri: Uri = generateUriForPhotoFile()
            activity!!.revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }

    private fun setData(task: Task) {
        fragmentEditTaskBinding.titleEdit.setText(task.mTitle)
        fragmentEditTaskBinding.titleFormEdit.isEnabled = false
        fragmentEditTaskBinding.descriptionEdit.setText(task.mDescription)
        fragmentEditTaskBinding.descriptionFormEdit.isEnabled = false
        val dateFormat = getDateFormat()
        fragmentEditTaskBinding.btnDateEdit.text = dateFormat.format(task.mDate)
        fragmentEditTaskBinding.btnDateEdit.isEnabled = false
        val timeFormat = getTimeFormat()
        fragmentEditTaskBinding.btnTimeEdit.text = timeFormat.format(task.mDate)
        fragmentEditTaskBinding.btnTimeEdit.isEnabled = false
        when {
            task.mState.equals("Todo", ignoreCase = true) -> {
                fragmentEditTaskBinding.radioBtnTodoEdit.isChecked = true
                mState = "Todo"
            }
            task.mState.equals("Doing", ignoreCase = true) -> {
                fragmentEditTaskBinding.radioBtnDoingEdit.isChecked = true
                mState = "Doing"
            }
            task.mState.equals("Done", ignoreCase = true) -> {
                fragmentEditTaskBinding.radioBtnDoneEdit.isChecked = true
                mState = "Done"
            }
        }
        fragmentEditTaskBinding.radioBtnTodoEdit.isEnabled = false
        fragmentEditTaskBinding.radioBtnDoingEdit.isEnabled = false
        fragmentEditTaskBinding.radioBtnDoneEdit.isEnabled = false
        fragmentEditTaskBinding.btnPicture.isEnabled = false
    }

    private fun listeners() {
        fragmentEditTaskBinding.btnEditEdit.setOnClickListener(View.OnClickListener {
            fragmentEditTaskBinding.titleFormEdit.isEnabled = true
            fragmentEditTaskBinding.descriptionFormEdit.isEnabled = true
            fragmentEditTaskBinding.btnDateEdit.isEnabled = true
            fragmentEditTaskBinding.btnTimeEdit.isEnabled = true
            fragmentEditTaskBinding.radioBtnTodoEdit.isEnabled = true
            fragmentEditTaskBinding.radioBtnDoingEdit.isEnabled = true
            fragmentEditTaskBinding.radioBtnDoneEdit.isEnabled = true
            fragmentEditTaskBinding.btnPicture.isEnabled = true
        })
        fragmentEditTaskBinding.btnSaveEdit.setOnClickListener(View.OnClickListener {
            if (fragmentEditTaskBinding.titleFormEdit.isEnabled) {
                if (validateInput()) {
                    editTask()
                    updateTasks(mTask)
                    sendResult()
                    dismiss()
                } else {
                    val strId = R.string.toast_insert
                    val toast = Toast.makeText(activity, strId, Toast.LENGTH_SHORT)
                    toast.show()
                }
            } else {
                dismiss()
            }
        })
        fragmentEditTaskBinding.btnDateEdit.setOnClickListener(View.OnClickListener {
            val datePickerFragment = DatePickerFragment.newInstance(mCalendar.time)

            //create parent-child relations between CDF and DPF
            datePickerFragment.setTargetFragment(
                this@EditTaskFragment,
                REQUEST_CODE_DATE_PICKER
            )
            datePickerFragment.show(
                activity!!.supportFragmentManager,
                FRAGMENT_TAG_DATE_PICKER
            )
        })
        fragmentEditTaskBinding.btnTimeEdit.setOnClickListener(View.OnClickListener {
            val timePickerFragment = TimePickerFragment.newInstance(mCalendar.time)
            timePickerFragment.setTargetFragment(
                this@EditTaskFragment,
                REQUEST_CODE_TIME_PICKER
            )
            timePickerFragment.show(
                activity!!.supportFragmentManager,
                FRAGMENT_TAG_TIME_PICKER
            )
        })
        fragmentEditTaskBinding.btnDeleteEdit.setOnClickListener(View.OnClickListener {
            mRepository.deleteTask(mTask)
            sendResult()
            dismiss()
        })
        if (sharedFeature == true) {
            fragmentEditTaskBinding.share.setOnClickListener(View.OnClickListener { shareIntent() })
        }
        fragmentEditTaskBinding.btnPicture.setOnClickListener(View.OnClickListener { takePictureIntent() })
    }

    private fun takePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (mPhotoFile != null && takePictureIntent
                    .resolveActivity(activity!!.packageManager) != null
            ) {

                // file:///data/data/com.example.ci/files/234234234234.jpg
                val photoUri = generateUriForPhotoFile()
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

    private fun updatePhotoView() {
        /*if (mPhotoFile == null || !mPhotoFile.exists()) return


        //this has a better memory management.
        val bitmap: Bitmap = PictureUtils.getScaledBitmap(
            mPhotoFile.absolutePath,
            activity
        )
        fragmentEditTaskBinding.taskPicture.setImageBitmap(bitmap)*/
    }

    private fun shareIntent() {
        val intentBuilder = IntentBuilder.from(activity!!)
        val intent = intentBuilder
            .setType("text/plain")
            .setText(shareWord())
            .setChooserTitle(getString(R.string.share))
            .createChooserIntent()
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun shareWord(): String? {
        val title = mTask.mTitle
        val description = mTask.mDescription
        val date = mTask.mDate.toString()
        val state: String = mState.toString()
        return getString(
            R.string.shareMassage,
            title,
            description,
            date,
            state
        )
    }

    private fun sendResult() {
        val fragment = targetFragment
        val requestCode = targetRequestCode
        val resultCode = Activity.RESULT_OK
        val intent = Intent()
        fragment!!.onActivityResult(requestCode, resultCode, intent)
    }

    private fun validateInput(): Boolean {
        return fragmentEditTaskBinding.titleEdit.text != null &&
            fragmentEditTaskBinding.descriptionEdit.text != null &&
            fragmentEditTaskBinding.btnDateEdit.text != null &&
            fragmentEditTaskBinding.btnTimeEdit.text != null &&
            (fragmentEditTaskBinding.radioBtnTodoEdit.isChecked ||
                    fragmentEditTaskBinding.radioBtnDoingEdit.isChecked
                    || fragmentEditTaskBinding.radioBtnDoneEdit.isChecked)
    }

    private fun editTask() {
        var state = ""
        when {
            fragmentEditTaskBinding.radioBtnTodoEdit.isChecked -> state = "Todo"
            fragmentEditTaskBinding.radioBtnDoingEdit.isChecked -> state = "Doing"
            fragmentEditTaskBinding.radioBtnDoneEdit.isChecked -> state = "Done"
        }
        mTask.mTitle = fragmentEditTaskBinding.titleEdit.text.toString()
        mTask.mDescription = fragmentEditTaskBinding.descriptionEdit.text.toString()
        mTask.mDate = mCalendar.time
        mTask.mState=state
    }

    private fun updateTasks(task: Task) {
        mRepository.updateTask(task)
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
        fragmentEditTaskBinding.btnDateEdit.text = dateFormat.format(userSelectedDate)
    }

    private fun updateTaskTime(userSelectedTime: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = userSelectedTime
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        mCalendar[Calendar.HOUR_OF_DAY] = hour
        mCalendar[Calendar.MINUTE] = minute
        val timeFormat = getTimeFormat()
        fragmentEditTaskBinding.btnTimeEdit.text = timeFormat.format(userSelectedTime)
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
        fun newInstance(taskId: UUID, shareFeature: Boolean) =
            EditTaskFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARGUMENT_TASK_ID, taskId)
                    putBoolean(ARGUMENT_SHARE_FEATURE, shareFeature)
                }
            }
    }
}