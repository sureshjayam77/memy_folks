package com.memy.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.memy.R
import com.memy.adapter.HelpAdapter
import com.memy.databinding.HelpActivityBinding
import com.memy.pojo.HelpItemObj
import com.memy.viewModel.FeedbackViewModel

class HelpActivity : AppBaseActivity() {

    private lateinit var binding : HelpActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.help_activity)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener {
            onBackPressed()
        }

        setupAdapter()
    }

    private fun setupAdapter(){
        val list = arrayListOf<HelpItemObj>()
        list.add(HelpItemObj("How to add family member",R.drawable.guide_1))
        list.add(HelpItemObj("How to invite family member",R.drawable.guide_2))
        list.add(HelpItemObj("Add many generations. No limits",R.drawable.guide_3))
        list.add(HelpItemObj("Add many family members. No limits",R.drawable.guide_4))
        list.add(HelpItemObj("How to add story",R.drawable.guide_5))
        val helpAdapter = HelpAdapter(this,list)
        binding.recyclerView.adapter = helpAdapter

    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {

    }
}