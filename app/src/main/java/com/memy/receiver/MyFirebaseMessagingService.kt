package com.memy.receiver

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.graphics.Bitmap


import android.media.RingtoneManager

import android.app.NotificationManager

import android.app.NotificationChannel
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.memy.R
import android.graphics.BitmapFactory
import android.text.TextUtils
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException

import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage != null) {
                var notificationTitle = ""
                var notificationContent = ""
                var imageUrl = ""
                // remoteMessage object contains all you need ;-)
                val dataStr: String? = remoteMessage.data.toString()
                try {
                    val obj = JSONObject(dataStr)
                    notificationTitle = obj.optString("title")
                    notificationContent = obj.optString("message")
                    imageUrl = obj.optString("imageUrl")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (TextUtils.isEmpty(notificationTitle)) {
                    notificationTitle = ""
                }
                if (TextUtils.isEmpty(notificationContent)) {
                    notificationContent = ""
                }
                if (TextUtils.isEmpty(imageUrl)) {
                    imageUrl = ""
                }
                // lets create a notification with these data
                createNotification(notificationTitle, notificationContent, imageUrl)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun createNotification(
        notificationTitle: String,
        notificationContent: String,
        imageUrl: String
    ) {
        // Let's create a notification builder object
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, resources.getString(R.string.notification_channel_id))
        // Create a notificationManager object
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // If android version is greater than 8.0 then create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create a notification channel
            val notificationChannel = NotificationChannel(
                resources.getString(R.string.notification_channel_id),
                resources.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Set properties to notification channel
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)

            // Pass the notificationChannel object to notificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        // Set the notification parameters to the notification builder object
        builder.setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
        // Set the image for the notification
        if (imageUrl != null) {
            val bitmap: Bitmap = getBitmapfromUrl(imageUrl)!!
            if(bitmap != null) {
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                ).setLargeIcon(bitmap)
            }
        }
        notificationManager.notify(1, builder.build())
    }

    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}