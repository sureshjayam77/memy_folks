package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.LoginReqObj
import com.memy.pojo.MobileNumberVerifyResObj
import com.memy.pojo.OTPVerificationResObj
import com.memy.pojo.ProfileVerificationResObj
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository : BaseRepository(){

     var loginResponse = MutableLiveData<MobileNumberVerifyResObj>()
    var otpVerifyResObj = MutableLiveData<ProfileVerificationResObj>()

    fun verifyMobileNumber(obj : LoginReqObj){
        val mobileNumberVerifyCall = retrofit.create(APIInterface::class.java).verifyMobileNumber(obj)
        mobileNumberVerifyCall?.enqueue(object :  Callback<MobileNumberVerifyResObj?>{
            override fun onResponse(
                call: Call<MobileNumberVerifyResObj?>?,
                response: Response<MobileNumberVerifyResObj?>?
            ) {
                loginResponse.value = response?.body()
            }

            override fun onFailure(call: Call<MobileNumberVerifyResObj?>?, t: Throwable) {
                loginResponse.value = MobileNumberVerifyResObj(null,0,null)
            }
        })
    }

    fun verifyOTP(obj : LoginReqObj){
        val mobileNumberVerifyCall = retrofit.create(APIInterface::class.java).verifyOTP(obj)
        mobileNumberVerifyCall?.enqueue(object :  Callback<ProfileVerificationResObj?>{
            override fun onResponse(
                call: Call<ProfileVerificationResObj?>?,
                response: Response<ProfileVerificationResObj?>?
            ) {
                otpVerifyResObj.value = response?.body()
            }

            override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                otpVerifyResObj.value = ProfileVerificationResObj(null,0,null)
            }
        })
    }
}

