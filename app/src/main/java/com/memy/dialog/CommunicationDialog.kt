package com.memy.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.memy.R
import com.memy.databinding.CommunicationDialogBinding
import com.memy.listener.DialogClickCallBack
import com.memy.pojo.CommunicationDialogData

open class CommunicationDialog : DialogFragment {

    private lateinit var binding: CommunicationDialogBinding
    private var communicationDialogData: CommunicationDialogData?
    private var listener: DialogClickCallBack?
    private var activity: AppCompatActivity?

    constructor(atc: AppCompatActivity?, data: CommunicationDialogData?, callBack: DialogClickCallBack?) {
        communicationDialogData = data
        listener = callBack
        activity = atc
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.communication_dialog, container, true)
        prepopulateData()
        isCancelable = false
        return binding.root
    }

    fun prepopulateData() {
        binding.msgContentTextView.setText(communicationDialogData?.messageContent)
        binding.positiveBtn.setText(communicationDialogData?.positiveBtnLabel)
        binding.negativeBtn.setText(communicationDialogData?.negativeBtnLabel)
        binding.positiveBtn.setOnClickListener(View.OnClickListener {
            listener?.dialogPositiveCallBack(communicationDialogData?.id)
        })
        binding.negativeBtn.setOnClickListener(View.OnClickListener {
            listener?.dialogNegativeCallBack()
        })

        binding.negativeBtn.visibility = if(TextUtils.isEmpty(communicationDialogData?.negativeBtnLabel)) (View.GONE) else(View.VISIBLE)
    }

    fun show() {
        show(activity?.supportFragmentManager!!, "")
    }

}