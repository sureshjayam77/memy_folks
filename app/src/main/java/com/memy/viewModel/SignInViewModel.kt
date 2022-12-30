package com.memy.viewModel

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.LoginReqObj
import com.memy.pojo.MobileNumberVerifyResObj
import com.memy.retrofit.LoginRepository
import com.memy.utils.Utils

class SignInViewModel : AppBaseViewModel() {
    var isEnableGenerateOtpBtn = MediatorLiveData<Boolean>()
    var isTermsChecked = MediatorLiveData<Boolean>()
    var phoneNumber = MutableLiveData<String>()
    var countryCode = MutableLiveData<String>()
    private val MAX_MOBILE_NUMBER_LENGTH: Int = 9
    private val loginRepository : LoginRepository
    var loginResponse : MutableLiveData<MobileNumberVerifyResObj>
    var otpSMSKey : String = ""

    init {
        isEnableGenerateOtpBtn.value = false
        isTermsChecked.value = false
        loginRepository  = LoginRepository()
        loginResponse = loginRepository.loginResponse
    }

    fun validateGenerateBtnEnable() {
        val cc: String? = countryCode.value
        val pn: String? = phoneNumber.value

        isEnableGenerateOtpBtn.value = ((!TextUtils.isEmpty(cc)) && (!TextUtils.isEmpty(pn)) && (Utils.isValidMobileNumber(cc,pn)) /*(pn?.length!! >= MAX_MOBILE_NUMBER_LENGTH)*/ && (isTermsChecked.value ?: false))
    }

    fun verifyMobileNumber(){
        val loginReqObj = LoginReqObj()
        loginReqObj?.mobile = phoneNumber.value
        loginReqObj?.country_code = countryCode.value
        loginReqObj?.key = otpSMSKey
        loginRepository.verifyMobileNumber(loginReqObj)
    }



}