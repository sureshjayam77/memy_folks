package com.memy.viewModel

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.LoginReqObj
import com.memy.pojo.MobileNumberVerifyResObj
import com.memy.pojo.OTPVerificationResObj
import com.memy.pojo.ProfileVerificationResObj
import com.memy.retrofit.LoginRepository

class OTPViewModel : AppBaseViewModel() {
    var isEnableVerifyOtpBtn: MediatorLiveData<Boolean> = MediatorLiveData()
    var otpCharOne: MutableLiveData<String> = MutableLiveData()
    var otpCharTwo: MutableLiveData<String> = MutableLiveData()
    var otpCharThree: MutableLiveData<String> = MutableLiveData()
    var otpCharFour: MutableLiveData<String> = MutableLiveData()
    var phoneNumber : MutableLiveData<String> = MutableLiveData()
    var countryCode : MutableLiveData<String> = MutableLiveData()
    var otpVerifyResObj : MutableLiveData<ProfileVerificationResObj>
    var loginResponse : MutableLiveData<MobileNumberVerifyResObj>
    private val loginRepository : LoginRepository
    var otpSMSKey : String = ""

    init {
        isEnableVerifyOtpBtn.value = false
        loginRepository  = LoginRepository()
        otpVerifyResObj = loginRepository.otpVerifyResObj
        loginResponse = loginRepository.loginResponse
    }

    fun validateVerifyBtnEnable() {
        val firstChar: String? = otpCharOne.value
        val secondChar: String? = otpCharTwo.value
        val thirdChar: String? = otpCharThree.value
        val fourthChar: String? = otpCharFour.value
        isEnableVerifyOtpBtn.value =
            ((!TextUtils.isEmpty(firstChar)) && (!TextUtils.isEmpty(secondChar)) && (!TextUtils.isEmpty(
                thirdChar
            )) && (!TextUtils.isEmpty(fourthChar)))
    }

    fun verifyOtp(){
        val loginReqObj = LoginReqObj()
        loginReqObj?.country_code = countryCode.value
        loginReqObj?.mobile = phoneNumber.value
        loginReqObj?.otp = otpCharOne.value+""+otpCharTwo.value+""+otpCharThree.value+""+otpCharFour.value
        loginRepository.verifyOTP(loginReqObj)
    }

    fun verifyMobileNumber(){
        val loginReqObj = LoginReqObj()
        loginReqObj?.mobile = phoneNumber.value
        loginReqObj?.country_code = countryCode.value
        loginReqObj?.key = otpSMSKey
        loginRepository.verifyMobileNumber(loginReqObj)
    }

}