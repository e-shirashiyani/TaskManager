package com.example.taskmanagerkotlin.view.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.Task
import com.example.taskmanagerkotlin.data.model.User
import com.example.taskmanagerkotlin.data.repository.IRepository
import com.example.taskmanagerkotlin.data.repository.IUserRepository
import com.example.taskmanagerkotlin.data.repository.TaskDBRepository
import com.example.taskmanagerkotlin.data.repository.UserDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentTabsBinding
import com.example.taskmanagerkotlin.view.fragment.DeleteAllFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_Username = "username"
private const val ARG_Password = "password"
private const val ARG_STATE = "state"
const val FRAGMENT_TAG_INSERT_TASK = "InsertTask"
const val REQUEST_CODE_INSERT_TASK = 0
const val FRAGMENT_TAG_EDIT_TASK = "EditTask"
const val REQUEST_CODE_EDIT_TASK = 1
const val FRAGMENT_TAG_DELETE_ALL_TASK = "DeleteAllTask"
const val REQUEST_CODE_DELETE_ALL_TASK = 2
class TabsFragment : Fragment() {
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var state: String
    private lateinit var fragmentTabsBinding: FragmentTabsBinding
    private var mAdapter: TabsAdapter? = null
    private lateinit var mRepository: IRepository
    private lateinit var mIUserRepository: IUserRepository
    private lateinit var mTasks: List<Task>
    private var isVisible: Boolean? = false
    private lateinit var mUser: User
    private lateinit var mActivity: FragmentActivity
    private var mFragment: TabsFragment = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username=it.getString(ARG_Username).toString()
            password=it.getString(ARG_Password).toString()
            state=it.getString(ARG_STATE).toString()
        }
        mActivity = activity as FragmentActivity
        mRepository = TaskDBRepository.getInstance(activity!!)!!
        mIUserRepository = UserDBRepository.getInstance(activity!!)!!
        mUser = mIUserRepository.getUser(Objects.requireNonNull(username), password)
    }

    override fun onPause() {
        super.onPause()
        updateUI()
        fragmentTabsBinding.fam.collapse()
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !isVisible!!) {

            updateUI()
        }
        isVisible = true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser && isVisible!!){
            val handler= Handler()
            handler.postDelayed({
                updateUI()
                                }, 500)
    }
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        if (requestCode == REQUEST_CODE_INSERT_TASK || requestCode == REQUEST_CODE_EDIT_TASK ||
                requestCode == REQUEST_CODE_DELETE_ALL_TASK) {
            updateUI()
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentTabsBinding = DataBindingUtil.inflate<FragmentTabsBinding>(
                inflater,
                R.layout.fragment_tabs, container, false
        )

        checkEmptyLayout()
        initViews()
        listeners()

        return fragmentTabsBinding.root
    }

    private fun initViews() {
        fragmentTabsBinding.recycler.layoutManager = LinearLayoutManager(activity)
        updateUI()
    }

    private fun listeners() {
        fragmentTabsBinding.fabInsert.setOnClickListener(View.OnClickListener {
            val insertTaskFragment: InsertTaskFragment =
                    InsertTaskFragment.newInstance(username.toString(), password.toString())
            insertTaskFragment.setTargetFragment(
                    this@TabsFragment,
                    REQUEST_CODE_INSERT_TASK
            )
            insertTaskFragment.show(
                    activity!!.supportFragmentManager,
                    FRAGMENT_TAG_INSERT_TASK
            )
        })
        fragmentTabsBinding.fabDelete.setOnClickListener(View.OnClickListener {
            val deleteAllFragment: DeleteAllFragment = DeleteAllFragment.newInstance()
            deleteAllFragment.setTargetFragment(
                    this@TabsFragment,
                    REQUEST_CODE_DELETE_ALL_TASK
            )
            deleteAllFragment.show(
                    activity!!.supportFragmentManager,
                    FRAGMENT_TAG_DELETE_ALL_TASK
            )
        })
        fragmentTabsBinding.fabLogOut.setOnClickListener(View.OnClickListener { activity!!.finish() })
    }

    private fun updateUI() {
        checkEmptyLayout()
        updateAdapter()
    }

    private fun updateAdapter() {
        if (mAdapter == null) {
            mAdapter = TabsAdapter(mTasks, mActivity,mFragment)
            fragmentTabsBinding.recycler.adapter = mAdapter
        } else {
            mAdapter!!.setTasks(mTasks)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun checkEmptyLayout() {
        when {
            state.equals("todo", ignoreCase = true) ->
                mTasks = mRepository.getTodoTask(mUser.primaryId)
            state.equals("doing", ignoreCase = true) ->
                mTasks = mRepository.getDoingTask(mUser.primaryId)
            state.equals("done", ignoreCase = true) ->
                mTasks = mRepository.getDoneTask(mUser.primaryId)
        }

        if (mTasks.isEmpty())
            fragmentTabsBinding.layoutEmpty.visibility = View.VISIBLE
        else fragmentTabsBinding.layoutEmpty.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(username: String, password: String, state: String) =
                TabsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_Username, username)
                        putString(ARG_Password, password)
                        putString(ARG_STATE, state)
                    }
                }
    }

    private class TabsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextViewTitle: TextView = itemView.findViewById(R.id.txtview_title)
        private val mTextViewDate: TextView = itemView.findViewById(R.id.txtview_date)
        private val mImageViewProfile: ImageView = itemView.findViewById(R.id.image_profile)
        private var mTask: Task? = null
        private lateinit var mActivity: FragmentActivity
        private lateinit var mFragment: TabsFragment
        fun bindTaskTabs(task: Task, activity: FragmentActivity,fragment: TabsFragment) {
            mTask = task
            mActivity = activity
            mFragment = fragment
            mTextViewTitle.text = task.mTitle
            val date = createDateFormat(task)
            mTextViewDate.text = date
            val color = Color.parseColor("#ff80aa")
            val string = task.mTitle!!.substring(0, 1)
            val drawable = TextDrawable.builder()
                    .buildRound(string, color)
            mImageViewProfile.setImageDrawable(drawable)
        }

        private fun createDateFormat(task: Task): String {
            var totalDate = ""
            val dateFormat = dateFormat
            val date = dateFormat.format(task.mDate)
            val timeFormat = timeFormat
            val time = timeFormat.format(task.mDate)
            totalDate = "$date  $time"
            return totalDate
        }

        private val dateFormat: DateFormat
            private get() = SimpleDateFormat("MMM dd,yyyy")
        private val timeFormat: DateFormat
            private get() = SimpleDateFormat("h:mm a")

        init {
            itemView.setOnClickListener {
                val editTaskFragment = EditTaskFragment.newInstance(mTask!!.mId!!, true)

                editTaskFragment.setTargetFragment(
                        mFragment,
                        REQUEST_CODE_EDIT_TASK
                )
                editTaskFragment.show(
                        mActivity.supportFragmentManager,
                        FRAGMENT_TAG_EDIT_TASK
                )
            }
        }
    }

    private class TabsAdapter(val tasks: List<Task>, val mActivity: FragmentActivity,
                              val mFragment: TabsFragment) :
            RecyclerView.Adapter<TabsHolder>() {

        private var mTasks: List<Task?> = tasks

        fun setTasks(taskList: List<Task>) {
            mTasks = taskList
        }

        override fun getItemCount(): Int {
            return mTasks.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabsHolder {
            val layoutInflater = LayoutInflater.from(mActivity)
            val view = layoutInflater.inflate(R.layout.task_row_list, parent, false)
            return TabsHolder(view)
        }

        override fun onBindViewHolder(holder: TabsHolder, position: Int) {
            val task = mTasks[position]
            if (task != null) {
                holder.bindTaskTabs(task, mActivity,mFragment)
            }
        }
    }
}