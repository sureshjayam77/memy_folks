package com.memy.viewModel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.AddStoryMediaObj
import com.memy.pojo.CommonResponse
import com.memy.pojo.ProfileData
import com.memy.retrofit.FeedbackRepository
import com.memy.retrofit.StoryRepository
import com.memy.utils.Constents

class FeedbackViewModel : AppBaseViewModel() {

    var enableSubmitBtn : MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    var storyTitle : MutableLiveData<String> = MutableLiveData<String>()
    var storyDesc : MutableLiveData<String> = MutableLiveData<String>()
    val feedbackRepository : FeedbackRepository = FeedbackRepository()
    var submitFeedbackRes = MutableLiveData<CommonResponse>()
    var userId : String = ""

    init {
        storyTitle.value = ""
        storyDesc.value = ""
        submitFeedbackRes = feedbackRepository.submitFeedbackRes
    }

    fun validateSubmitBtn(){
        var title = storyTitle.value?.trim()
        var desc = storyDesc.value?.trim()
        var enable = false

        if((!TextUtils.isEmpty(title)) && (!TextUtils.isEmpty(desc))){
            enable = true
        }
        enableSubmitBtn.value = enable
    }


}