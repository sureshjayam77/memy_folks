package com.memy.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.memy.fragment.GuideFragment
import com.memy.utils.Constents

class GuideFragmentAdapter(val fragmentManager: FragmentManager,val lifecycle: Lifecycle,val list : List<Int>?): FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int = if(list != null) (list.size) else 0

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = GuideFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(Constents.INTENT_BUNDLE_GUIDE_ARGUMENT_TAG, list?.get(position) ?: 0)
        }
        return fragment
    }

}