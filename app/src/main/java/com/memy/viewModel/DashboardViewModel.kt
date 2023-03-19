package com.memy.viewModel

import androidx.lifecycle.MutableLiveData
import com.memy.pojo.*
import com.memy.retrofit.AddFamilyRepository
import com.memy.retrofit.DashboardRepository
import com.memy.retrofit.StoryRepository

class DashboardViewModel : AppBaseViewModel() {
    var userData : MutableLiveData<ProfileData> = MutableLiveData()
    var isTreeView : MutableLiveData<Boolean> = MutableLiveData()
    var actionTitle : MutableLiveData<String> = MutableLiveData()
    var storyListRes = MutableLiveData<StoryListRes>()
    var profileResForEdit = MutableLiveData<ProfileVerificationResObj>()
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
    var showGuideListView : MutableLiveData<Boolean> = MutableLiveData()
    var showSocialLinkAddView : MutableLiveData<Boolean> = MutableLiveData()
    var showAddRelationView : MutableLiveData<Boolean> = MutableLiveData()
    var memberRelationData = MutableLiveData<MemberRelationShipResData>()
    var showProgressBar : MutableLiveData<Boolean> = MutableLiveData()
    var shareResData = MutableLiveData<ShareResponse>()
    var selectedMemberId : String? = ""
    var selectedMemberAction : Int? = 0
    var deleteAccountRes = MutableLiveData<CommonResponse>()
    var inviteCommonResData = MutableLiveData<CommonResponse>()
    var tabPos = 0 //Tree 0, story = 1,bubble = 2
    var updateAdminRes = MutableLiveData<CommonResponse>()
    var isDownloadFileCick = false
    var downloadURL = ""

    init {
        dashboardRepository = DashboardRepository()
        storyRepository = StoryRepository()
        addFamilyRepository = AddFamilyRepository()
        profileVerificationResObj = dashboardRepository.profileVerificationResObj
        updateFcmRes = dashboardRepository.updateFcmRes
        storyListRes = storyRepository.storyListRes
        profileResForEdit = dashboardRepository.profileResForEdit
        memberRelationData = dashboardRepository.memberRelationData
        deleteAccountRes = addFamilyRepository.deleteAccountRes
        profileSocialLinkUpdateRes = addFamilyRepository.profileSocialLinkUpdateRes
        inviteCommonResData = dashboardRepository.inviteCommonResData
        shareResData = dashboardRepository.shareResData
        updateAdminRes = dashboardRepository.updateAdminRes
        isTreeView.value = true
        showGuideListView.value = true
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

    fun fetchProfileForEdit(userId : Int?){
        dashboardRepository.fetchUserProfileForEdit(userId)
    }

    fun deleteAccount(id:Int?){
        addFamilyRepository.deleteAccount(id)
    }
    fun hideGuideView(){
        showGuideListView.value = false
    }

    fun inviteFamilyMember(userId : String?){
        dashboardRepository.inviteFamilyMember(userId)
    }

    fun shareFamilyMember(userId : String?){
        dashboardRepository.fetchShareData(userId)
    }

    fun updateAdminAccess(isRemoveAccess : Boolean,adminId : Int){
        if(isRemoveAccess) {
            dashboardRepository.removeShareAccess(
                adminId,
                profileResForEdit?.value?.data?.mid
            )
        }else{
            dashboardRepository.giveShareAccess(
                adminId,
                profileResForEdit?.value?.data?.mid
            )
        }
    }
}