package com.memy.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.CommonResponse
import com.memy.pojo.FeedbackReqObj
import com.memy.utils.Constents
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActionReceiver : BroadcastReceiver() {


    private var context: Context? = null

    override fun onReceive(ctx: Context?, intent: Intent?) {
        context = ctx?.getApplicationContext()
        if(intent != null) {
            val notificationId = intent!!.getIntExtra("notification_id",-1)
            val requestId = intent!!.getIntExtra("request_id",-1)
            val action = intent!!.getStringExtra("action");
            if(!TextUtils.isEmpty(action)) {
                val retrofit = BaseRepository().retrofit
                val submitFeedbackCall = retrofit.create(APIInterface::class.java).addFamilyAction(BaseRepository.APP_KEY_VALUE,requestId,action?.lowercase())
                submitFeedbackCall?.enqueue(object : Callback<CommonResponse?> {
                    override fun onResponse(
                        call: Call<CommonResponse?>?,
                        response: Response<CommonResponse?>?
                    ) {
                        val res = response?.body()
                        if((res != null) && (res. statusCode == 200) &&(res.data != null) ){
                            removeNotification(notificationId)
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                        Log.d("","")
                    }
                })
            }
        }
    }

    private fun removeNotification(notificationId : Int){
        try {
            val mNotificationManager =
                context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(notificationId)
        }catch (e : Exception){
            e.printStackTrace()
        }

    }
}