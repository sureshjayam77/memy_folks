package com.memy.viewModel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.AddStoryMediaObj
import com.memy.pojo.CommonResponse
import com.memy.pojo.ProfileData
import com.memy.retrofit.StoryRepository
import com.memy.utils.Constents

class AddStoryViewModel : AppBaseViewModel() {

    var enableAddStoryBtn : MutableLiveData<Boolean>  = MutableLiveData<Boolean>()
    var storyTitle : MutableLiveData<String> = MutableLiveData<String>()
    var storyDesc : MutableLiveData<String> = MutableLiveData<String>()
    var storyAccess : MutableLiveData<String> = MutableLiveData()
    var storyMedia : MutableLiveData<ArrayList<AddStoryMediaObj>> = MutableLiveData()
    var userData : ProfileData? = null
    val addStoryRepository : StoryRepository = StoryRepository()
    var addStoryRes = MutableLiveData<CommonResponse>()

    init {
        storyTitle.value = ""
        storyDesc.value = ""
        storyAccess.value = Constents.STORY_ACCESS_PRIVATE
        storyMedia.value = ArrayList()
        addStoryRes = addStoryRepository.addStoryRes
    }

    fun validateSubmitBtn(){
        var title = storyTitle.value?.trim()
        var desc = storyDesc.value?.trim()
        var storyAccess = storyAccess.value
        var storyMedia = storyMedia.value
        var enable = false

        if((!TextUtils.isEmpty(title)) && (!TextUtils.isEmpty(desc)) && (storyMedia?.size!! > 0)){
            enable = true
        }
        enableAddStoryBtn.value = enable
    }


}