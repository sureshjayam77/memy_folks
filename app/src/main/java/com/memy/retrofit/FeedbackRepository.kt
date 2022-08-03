package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody


class FeedbackRepository : BaseRepository() {

    var submitFeedbackRes = MutableLiveData<CommonResponse>()

    fun submitFeedback(req : FeedbackReqObj){
        val submitFeedbackCall = retrofit.create(APIInterface::class.java).submitFeedback(req,BaseRepository.APP_KEY_VALUE)
        submitFeedbackCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                submitFeedbackRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                submitFeedbackRes.value = CommonResponse(null,0,null)
            }
        })
    }

}