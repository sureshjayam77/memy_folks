package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.ProfileVerificationResObj
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardRepository : BaseRepository() {

    var profileVerificationResObj = MutableLiveData<ProfileVerificationResObj>()

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
}