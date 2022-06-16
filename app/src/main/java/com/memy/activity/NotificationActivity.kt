package com.memy.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.memy.R
import com.memy.adapter.NotificationListAdapter
import com.memy.databinding.ActivityNotificationBinding
import com.memy.databinding.AddFamilyActivityBinding
import com.memy.utils.Constents
import com.memy.viewModel.NotificationViewModel


class NotificationActivity : AppBaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityNotificationBinding
    lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupViewModel()
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {
    }


    private fun setupViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(this)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.notificationRv.layoutManager = linearLayoutManager
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        val mid = intent?.getIntExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, -1)
        viewModel.notificationRepository.fetchNotifications(mid!!)
        viewModel.notificationRepository.notificationRes.observe(this, {
            if (it.data != null && it.data.size > 0) {
                binding.dataNoTxt.visibility = View.GONE
                binding.notificationRv.adapter = NotificationListAdapter(this, it.data)
            } else {
                binding.dataNoTxt.visibility = View.VISIBLE
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.backIconImageView -> {
               onBackPressed()
            }
        }
    }


}


