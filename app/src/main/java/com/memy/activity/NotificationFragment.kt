package com.memy.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.memy.R
import com.memy.adapter.NotificationListAdapter
import com.memy.databinding.FragmentNotificationBinding
import com.memy.fragment.BaseFragment
import com.memy.utils.Constents
import com.memy.viewModel.NotificationViewModel


class NotificationFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentNotificationBinding
    lateinit var viewModel: NotificationViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewBinding()
        setupViewModel()
    }
    private fun setupViewBinding() {
        binding.lifecycleOwner = this
        binding.backIconImageView.setOnClickListener(this)
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.notificationRv.layoutManager = linearLayoutManager
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        val mid = arguments?.getInt(Constents.FAMILY_MEMBER_ID_INTENT_TAG, -1)
        viewModel.notificationRepository.fetchNotifications(mid!!)
        viewModel.notificationRepository.notificationRes.observe(requireActivity(), {
            if (it.data != null && it.data.size > 0) {
                binding.dataNoTxt.visibility = View.GONE
                binding.notificationRv.adapter = NotificationListAdapter(requireActivity(), it.data)
            } else {
                binding.dataNoTxt.visibility = View.VISIBLE
            }
        })
    }

    override fun onClick(p0: View?) {

    }


}


