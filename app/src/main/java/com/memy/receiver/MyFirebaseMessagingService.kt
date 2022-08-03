package com.memy.receiver


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.memy.R
import com.memy.activity.SplashActivity
import com.memy.utils.Constents
import org.json.JSONException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var r: Random
    val min = 0
    val max = 80

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onCreate() {
        super.onCreate()

        r = Random()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage != null) {
                var notificationTitle = ""
                var notificationContent = ""
                var requestId = -1
                var notificationType = 0
                // remoteMessage object contains all you need ;-)
                try {
                    //  val obj = JSONObject(dataStr)
                    notificationTitle = remoteMessage.getData().get("title").toString()
                    notificationContent = remoteMessage.getData().get("message").toString()
                    requestId = remoteMessage.getData().get("mri")?.toInt() ?: -1
                    notificationType = remoteMessage.getData().get("pnt")?.toInt() ?: 0
                    //  imageUrl = remoteMessage.getData().get("imageurl").toString()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (TextUtils.isEmpty(notificationTitle)) {
                    notificationTitle = ""
                }
                if (TextUtils.isEmpty(notificationContent)) {
                    notificationContent = ""
                }
                // lets create a notification with these data
                createNotification(notificationTitle, notificationContent, requestId, notificationType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotification(
        notificationTitle: String,
        notificationContent: String,
        requestId: Int,
        notificationType: Int
    ) {
        val notifId = Random().nextInt()
        // Let's create a notification builder object
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, resources.getString(R.string.notification_channel_id))

        val notifyIntent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notifyIntent.putExtra(
            Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK,
            Constents.DEEPLINK_NOTIFICATION
        )
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        builder.setContentIntent(notifyPendingIntent)

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
            .setSmallIcon(R.mipmap.notification_icon)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
        if ((notificationType != null) && (notificationType == Constents.NOTIFICATION_TYPE_ADD_FAMILY_MEMBER)) {
            val positiveBroadcastIntent = Intent(this, NotificationActionReceiver::class.java)
            positiveBroadcastIntent.putExtra("notification_id", notifId)
            positiveBroadcastIntent.putExtra("request_id", requestId)
            positiveBroadcastIntent.putExtra("action", Constents.NOTIFICATION_ACTION_ACCEPT)
            val positiveActionIntent = PendingIntent.getBroadcast(
                this,
                Random().nextInt(),
                positiveBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )


            val negativeBroadcastIntent = Intent(this, NotificationActionReceiver::class.java)
            negativeBroadcastIntent.putExtra("notification_id", notifId)
            negativeBroadcastIntent.putExtra("request_id", requestId)
            negativeBroadcastIntent.putExtra("action", Constents.NOTIFICATION_ACTION_REJECT)
            val negativeActionIntent = PendingIntent.getBroadcast(
                this,
                Random().nextInt(),
                negativeBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            builder.addAction(0, "Accept", positiveActionIntent)
                .addAction(0, "Reject", negativeActionIntent)
        }
        // Set the image for the notification
        /* if (imageUrl != null) {
             val bitmap: Bitmap = getBitmapfromUrl(imageUrl)!!
             if(bitmap != null) {
                 builder.setStyle(
                     NotificationCompat.BigPictureStyle()
                         .bigPicture(bitmap)
                         .bigLargeIcon(null)
                 ).setLargeIcon(bitmap)
             }
         }*/
        notificationManager.notify(notifId, builder.build())
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