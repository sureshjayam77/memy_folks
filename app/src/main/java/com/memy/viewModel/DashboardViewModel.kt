package com.memy.viewModel

import androidx.lifecycle.MutableLiveData
import com.memy.pojo.*
import com.memy.retrofit.DashboardRepository
import com.memy.retrofit.StoryRepository

class DashboardViewModel : AppBaseViewModel() {
    var userData : MutableLiveData<ProfileData> = MutableLiveData()
    var isTreeView : MutableLiveData<Boolean> = MutableLiveData()
    var storyListRes = MutableLiveData<StoryListRes>()
    var profileVerificationResObj = MutableLiveData<ProfileVerificationResObj>()
    var dashboardRepository : DashboardRepository
    var storyRepository : StoryRepository
    var showProfile : Boolean? = false
    var storyListData : ArrayList<StoryFeedData> = ArrayList()
    val storyFetchLimit  = 50
    var storyFetchingCurrentPage = 0
    var isLoadingProfilePage = false
    var totalItemCount = 0
    var lastVisibleItem = 0
    var visibleThreshold = 3
    var pageNumber = 1
    var feedLimit = 10
    var isTreeSwitched = false
    var updateFcmRes = MutableLiveData<CommonResponse>()
    var fcmStrKey = ""

    init {
        dashboardRepository = DashboardRepository()
        storyRepository = StoryRepository()
        profileVerificationResObj = dashboardRepository.profileVerificationResObj
        updateFcmRes = dashboardRepository.updateFcmRes
        storyListRes = storyRepository.storyListRes
        isTreeView.value = true
    }

    fun fetchProfile(userId : Int?){
        dashboardRepository.fetchUserProfile(userId)
    }

    fun updateFCMToken(req : FCMTokenUpdateReq?){
        dashboardRepository.updateFCMToken(req)
    }

    fun fetchAllStory(userId : Int?,familyMemId : Int?,pageNumber : Int?,itemLimit : Int?){
        storyRepository.fetchStory(userId,0,pageNumber,itemLimit)
    }


}