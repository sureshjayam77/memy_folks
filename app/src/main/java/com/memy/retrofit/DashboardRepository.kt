package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository : BaseRepository() {

    var memberRelationData = MutableLiveData<MemberRelationShipResData>()
    var profileVerificationResObj = MutableLiveData<ProfileVerificationResObj>()
    var profileResForEdit = MutableLiveData<ProfileVerificationResObj>()
    var inviteCommonResData = MutableLiveData<CommonResponse>()
    var shareResData = MutableLiveData<ShareResponse>()
    var updateFcmRes = MutableLiveData<CommonResponse>()
    var updateAdminRes = MutableLiveData<CommonResponse>()

    fun fetchUserProfile(userId : Int?){
        val mobileNumberVerifyCall = retrofit.create(APIInterface::class.java).fetchProfile(BaseRepository.APP_KEY_VALUE,userId)
        mobileNumberVerifyCall?.enqueue(object : Callback<ProfileVerificationResObj?> {
            override fun onResponse(
                call: Call<ProfileVerificationResObj?>?,
                response: Response<ProfileVerificationResObj?>?
            ) {
                profileVerificationResObj.value = response?.body()
            }

            override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                profileVerificationResObj.value = ProfileVerificationResObj(null,0,null)
            }
        })
    }

    fun fetchUserProfileForEdit(userId : Int?){
        val profileCall = retrofit.create(APIInterface::class.java).fetchProfile(BaseRepository.APP_KEY_VALUE,userId)
        profileCall?.enqueue(object : Callback<ProfileVerificationResObj?> {
            override fun onResponse(
                call: Call<ProfileVerificationResObj?>?,
                response: Response<ProfileVerificationResObj?>?
            ) {
                profileResForEdit.value = response?.body()
            }

            override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                profileResForEdit.value = ProfileVerificationResObj(null,0,null)
            }
        })
    }

    fun fetchUserProfile(userId : Int?,ownerId : Int?){
        val mobileNumberVerifyCall = retrofit.create(APIInterface::class.java).fetchProfile(userId,BaseRepository.APP_KEY_VALUE,ownerId)
        mobileNumberVerifyCall?.enqueue(object : Callback<ProfileVerificationResObj?> {
            override fun onResponse(
                call: Call<ProfileVerificationResObj?>?,
                response: Response<ProfileVerificationResObj?>?
            ) {
                profileVerificationResObj.value = response?.body()
            }

            override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                profileVerificationResObj.value = ProfileVerificationResObj(null,0,null)
            }
        })
    }

    fun updateFCMToken(req: FCMTokenUpdateReq?) {
        val updateFCMTokenCall = retrofit.create(APIInterface::class.java)
            .updateFCMToken(req,BaseRepository.APP_KEY_VALUE)
        updateFCMTokenCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                updateFcmRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                updateFcmRes.value = CommonResponse(null, 0, null)
            }
        })
    }

    fun fetchMemberRelationData(userId : String?){
        val mobileNumberVerifyCall = retrofit.create(APIInterface::class.java).getMemberRelationShip(BaseRepository.APP_KEY_VALUE,userId)
        mobileNumberVerifyCall?.enqueue(object : Callback<MemberRelationShipResData?> {
            override fun onResponse(
                call: Call<MemberRelationShipResData?>?,
                response: Response<MemberRelationShipResData?>?
            ) {
                memberRelationData.value = response?.body()
            }

            override fun onFailure(call: Call<MemberRelationShipResData?>?, t: Throwable) {
                memberRelationData.value = MemberRelationShipResData(0,null,null)
            }
        })
    }

    fun inviteFamilyMember(userId : String?){
        val profileCall = retrofit.create(APIInterface::class.java).inviteFamilyMember(BaseRepository.APP_KEY_VALUE,userId)
        profileCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                inviteCommonResData.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                inviteCommonResData.value = CommonResponse(null,null,null)
            }
        })
    }
    fun fetchShareData(userId : String?){
        val profileCall = retrofit.create(APIInterface::class.java).fetchShareData(BaseRepository.APP_KEY_VALUE,userId)
        profileCall?.enqueue(object : Callback<ShareResponse?> {
            override fun onResponse(
                call: Call<ShareResponse?>?,
                response: Response<ShareResponse?>?
            ) {
                shareResData.value = response?.body()
            }

            override fun onFailure(call: Call<ShareResponse?>?, t: Throwable) {
                shareResData.value = ShareResponse(null,null,null)
            }
        })
    }

    fun giveShareAccess(parentId : Int?,childId : Int?){
        val profileCall = retrofit.create(APIInterface::class.java).updateAdminAccess(BaseRepository.APP_KEY_VALUE,parentId,childId)
        profileCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                updateAdminRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                updateAdminRes.value = CommonResponse(null,null,null)
            }
        })
    }

    fun removeShareAccess(parentId : Int?,childId : Int?){
        val profileCall = retrofit.create(APIInterface::class.java).removeAdminAccess(BaseRepository.APP_KEY_VALUE,parentId,childId)
        profileCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                updateAdminRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                updateAdminRes.value = CommonResponse(null,null,null)
            }
        })
    }
}