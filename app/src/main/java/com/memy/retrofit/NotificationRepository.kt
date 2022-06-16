package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.NotificationRes
import com.memy.pojo.StoryListRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository:BaseRepository() {
    var notificationRes = MutableLiveData<NotificationRes>()
    fun fetchNotifications(familyMemberId : Int){
        var relationShipCall : Call<NotificationRes?>?
        relationShipCall = retrofit.create(APIInterface::class.java).getNotifications(familyMemberId.toString(),APP_KEY_VALUE)
        relationShipCall?.enqueue(object : Callback<NotificationRes?> {
            override fun onResponse(
                call: Call<NotificationRes?>?,
                response: Response<NotificationRes?>?
            ) {
                notificationRes.value = response?.body()
            }
            override fun onFailure(call: Call<NotificationRes?>?, t: Throwable) {
                notificationRes.value = NotificationRes(null,0,null)
            }
        })
    }
}