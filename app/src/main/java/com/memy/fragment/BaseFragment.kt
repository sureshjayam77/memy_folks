package com.memy.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.memy.utils.PreferenceHelper

abstract class BaseFragment : Fragment() {
    protected lateinit var prefhelper : PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefhelper = PreferenceHelper().getInstance(requireActivity())
    }
}