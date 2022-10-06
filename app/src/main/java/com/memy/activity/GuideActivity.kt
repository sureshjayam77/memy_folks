package com.memy.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.memy.R
import com.memy.adapter.GuideFragmentAdapter
import com.memy.databinding.GuideLayoutBinding
import com.memy.utils.Constents
import com.memy.viewModel.DashboardViewModel

class GuideActivity : AppBaseActivity() {
    lateinit var binding : GuideLayoutBinding
    lateinit var viewModel : DashboardViewModel
    var guideSelectedPos = 0
    val guideImageList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.guide_layout)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.viewModel = viewModel
        guideSelectedPos = intent.getIntExtra(Constents.INTENT_BUNDLE_GUIDE_ARGUMENT_POS_TAG,0) ?: 0
        guideShowEnable()
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {

    }


    private fun guideShowEnable(){
        viewModel.showGuideListView.value = true
        guideImageList.add(R.drawable.guide_1)
        guideImageList.add(R.drawable.guide_2)
        guideImageList.add(R.drawable.guide_3)
        guideImageList.add(R.drawable.guide_4)
        guideImageList.add(R.drawable.guide_5)
        val adapter = GuideFragmentAdapter(supportFragmentManager,lifecycle, guideImageList)
        binding.guideViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.guideViewPager) { tab, position ->
            //Some implementation
        }.attach()
        binding.guideViewPager.postDelayed(Runnable {
            binding.guideViewPager.setCurrentItem(guideSelectedPos,true)
        },100)
    }

    fun closeActicity(v: View){
        viewModel.hideGuideView()
        onBackPressed()
    }

    fun nextClick(v:View){
        val currentPos = binding.guideViewPager.currentItem
        val nextPos = currentPos+1
        if(guideImageList.size > currentPos){
            binding.guideViewPager.postDelayed(Runnable {
                binding.prevImageView.alpha = 1f
                binding.guideViewPager.setCurrentItem(nextPos,true)
            },100)
        }
        if(nextPos == guideImageList.size){
            binding.nextImageView.alpha = 0.5f
        }
    }

    fun prevClick(v:View){
        val currentPos = binding.guideViewPager.currentItem
        val nextPos = currentPos-1
        if(0 < currentPos){
            binding.guideViewPager.postDelayed(Runnable {
                binding.nextImageView.alpha = 1f
                binding.guideViewPager.setCurrentItem(nextPos,true)
            },100)
        }

        if(nextPos == 0){
            binding.prevImageView.alpha = 0.5f
        }
    }
}