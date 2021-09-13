package com.example.taskmanagerkotlin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taskmanagerkotlin.view.fragment.TabsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, var mNumOfTabs: Int,
                       private val username: String?,
                       private val password: String?) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TabsFragment.newInstance(username!!, password!!, "todo")
            1 -> TabsFragment.newInstance(username!!, password!!, "doing")
            2 -> TabsFragment.newInstance(username!!, password!!, "done")
            else -> TODO()
        }
    }

    override fun getItemCount(): Int {
        return mNumOfTabs
    }
}