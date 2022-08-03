package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.memy.R
import com.memy.databinding.FeedbackActivityBinding
import com.memy.pojo.CommonResponse
import com.memy.pojo.FeedbackReqObj
import com.memy.viewModel.FeedbackViewModel
import androidx.lifecycle.Observer

class FeedbackActivity : AppBaseActivity() {

    private lateinit var viewModel : FeedbackViewModel
    private lateinit var binding : FeedbackActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.feedback_activity)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
        binding.viewModel = viewModel
        initObserver()
    }

    fun initObserver(){
        viewModel.storyTitle.observe(this, Observer { v ->
            viewModel.validateSubmitBtn()
        })
        viewModel.storyDesc.observe(this, Observer { v ->
            viewModel.validateSubmitBtn()
        })

        viewModel.submitFeedbackRes.observe(this, this::validateSubmitRes)
    }

    private fun validateSubmitRes(res: CommonResponse){
        hideProgressBar()
        if (res != null) {
            if ((res.statusCode == 200) || (res.statusCode == 201)) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.submit_feedback_success_id,
                        getString(R.string.feedback_submit_success_fully),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }

    private fun errorHandler(res: CommonResponse) {
        var message = ""
        if ((res != null) && (res.errorDetails != null)) {
            message = res.errorDetails.message!!
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.something_went_wrong)
        }
        showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
    }

    fun submitfeedback(v:View){
        val subject = viewModel.storyTitle.value
        val comments = viewModel.storyDesc.value
        if((!TextUtils.isEmpty(subject)) && (!TextUtils.isEmpty(comments)) && (prefhelper.fetchUserData() != null) && (!TextUtils.isEmpty(subject?.trim())) && (!TextUtils.isEmpty(comments?.trim())) ){
            showProgressBar()
            viewModel.feedbackRepository.submitFeedback(FeedbackReqObj(prefhelper.fetchUserData()?.mid,subject,comments))
        }
    }

    private fun showProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.GONE

    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
       if (id == R.id.submit_feedback_success_id) {
            var intent = Intent()
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }
}