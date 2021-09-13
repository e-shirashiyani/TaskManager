package com.example.taskmanagerkotlin.view.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.adapter.ViewPagerAdapter
import com.example.taskmanagerkotlin.data.model.User
import com.example.taskmanagerkotlin.data.repository.IUserRepository
import com.example.taskmanagerkotlin.data.repository.UserDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentTaskListBinding
import com.google.android.material.tabs.TabLayout


private const val ARG_Username = "username";
private const val ARG_Password = "password";

class TaskListFragment : Fragment() {
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var fragmentTaskListBinding: FragmentTaskListBinding
    private lateinit var mRepository: IUserRepository
    private lateinit var mUser: User
    private lateinit var mViewPagerAdapter: ViewPagerAdapter
    private lateinit var mFragmentTodo: TabsFragment
    private  lateinit var mFragmentDoing:TabsFragment
    private  lateinit var mFragmentDone:TabsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_Username).toString()
            password = it.getString(ARG_Password).toString()
        }
        setHasOptionsMenu(true)
        mFragmentTodo = TabsFragment.newInstance(username, password, "todo")
        mFragmentDoing = TabsFragment.newInstance(username, password, "doing")
        mFragmentDone = TabsFragment.newInstance(username, password, "done")
        mRepository = UserDBRepository.getInstance(activity!!)!!
        mUser = mRepository.getUser(username, password)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_task_list, menu)
        updateSubtitle()
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_search -> {
                val search = item.tooltipText as String
                *//*val intent: Intent =
                    SearchActivity.newIntent(activity, search, mUser!!.getPrimaryId())
                startActivity(intent)*//*
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun updateSubtitle() {
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.subtitle = username
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentTaskListBinding = DataBindingUtil.inflate<FragmentTaskListBinding>(
            inflater,
            R.layout.fragment_task_list, container, false
        )

        updateView()
        initTab()
        return fragmentTaskListBinding.root
    }

    private fun updateView() {
        fragmentTaskListBinding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        mFragmentTodo.userVisibleHint = true
                    }
                    1 -> {
                        mFragmentDoing.userVisibleHint = true
                    }
                    else -> {
                        mFragmentDone.userVisibleHint = true
                    }
                }
            }
        })
    }

    private fun initTab() {
        addTabs()
        fragmentTaskListBinding.tabs.tabGravity = TabLayout.GRAVITY_FILL
        mViewPagerAdapter = ViewPagerAdapter(activity!!,
            fragmentTaskListBinding.tabs.tabCount, username, password
        )

        fragmentTaskListBinding.viewpager.adapter = mViewPagerAdapter
        fragmentTaskListBinding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                fragmentTaskListBinding.tabs.setScrollPosition(position, 0f, true)
            }
        })
        fragmentTaskListBinding.tabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fragmentTaskListBinding.viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun addTabs() {
        fragmentTaskListBinding.tabs.addTab(fragmentTaskListBinding.tabs.newTab().setText("TODO"))
        fragmentTaskListBinding.tabs.addTab(fragmentTaskListBinding.tabs.newTab().setText("DOING"))
        fragmentTaskListBinding.tabs.addTab(fragmentTaskListBinding.tabs.newTab().setText("DONE"))
    }

    companion object {
        @JvmStatic
        fun newInstance(username: String, password: String) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_Username, username)
                    putString(ARG_Password, password)
                }
            }
    }
}