package com.memy.viewModel

import androidx.lifecycle.MutableLiveData
import com.memy.pojo.*
import com.memy.retrofit.AddFamilyRepository
import com.memy.retrofit.DashboardRepository
import com.memy.retrofit.StoryRepository

class DashboardViewModel : AppBaseViewModel() {
    var userData : MutableLiveData<ProfileData> = MutableLiveData()
    var isTreeView : MutableLiveData<Boolean> = MutableLiveData()
    var storyListRes = MutableLiveData<StoryListRes>()
    var profileVerificationResObj = MutableLiveData<ProfileVerificationResObj>()
    var profileSocialLinkUpdateRes = MutableLiveData<CommonResponse>()
    var dashboardRepository : DashboardRepository
    var storyRepository : StoryRepository
    var addFamilyRepository : AddFamilyRepository
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
    var instagramLink : MutableLiveData<String> = MutableLiveData()
    var facebookLink : MutableLiveData<String> = MutableLiveData()
    var twitterLink : MutableLiveData<String> = MutableLiveData()
    var linkedInLink : MutableLiveData<String> = MutableLiveData()
    var aboutContent : MutableLiveData<String> = MutableLiveData()
    var showSocialLinkAddView : MutableLiveData<Boolean> = MutableLiveData()
    var showAddRelationView : MutableLiveData<Boolean> = MutableLiveData()
    var selectedMemberId : String? = ""

    init {
        dashboardRepository = DashboardRepository()
        storyRepository = StoryRepository()
        addFamilyRepository = AddFamilyRepository()
        profileVerificationResObj = dashboardRepository.profileVerificationResObj
        updateFcmRes = dashboardRepository.updateFcmRes
        storyListRes = storyRepository.storyListRes
        profileSocialLinkUpdateRes = addFamilyRepository.profileSocialLinkUpdateRes
        isTreeView.value = true
        showAddRelationView.value = false
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

    fun hideSocialMediaLinkAddView(){
        showSocialLinkAddView.value = false
        instagramLink.value = userData.value?.instagram_link
        facebookLink.value = userData.value?.facebook_link
        twitterLink.value = userData.value?.twitter_link
        linkedInLink.value = userData.value?.linkedin_link
        aboutContent.value = userData.value?.about_me
    }

    fun saveSocialMediaLink(userId : Int?){
        addFamilyRepository.updateSocialMediaLinks(userId,instagramLink.value,facebookLink.value,twitterLink.value,linkedInLink.value,aboutContent.value)
    }

    fun showSocialMediaLinkAddView() {
        showSocialLinkAddView.value = true
    }

    fun fetchMemberRelationShip(userId : String?){
        dashboardRepository.fetchMemberRelationData(userId)
    }
}