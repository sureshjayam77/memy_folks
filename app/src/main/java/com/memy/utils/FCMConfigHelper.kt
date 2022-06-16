package com.memy.utils

import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.memy.R
import com.memy.listener.FirebaseCallBack
import java.util.*

class FCMConfigHelper {
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private val timer = Timer()
    private var FCM_TRIGGER_COUNT = 1
    private var callBack: FirebaseCallBack? = null

    fun getFCMRemoteConfigData(keyName: Array<String?>?, listener: FirebaseCallBack?) {
        if (keyName != null && keyName.size > 0 && !TextUtils.isEmpty(keyName[0])) {
            FCM_TRIGGER_COUNT = 1
            triggerFCM(keyName)
            callBack = listener
        }
    }

    private fun triggerFCM(keyName: Array<String?>) {
        if (firebaseRemoteConfig == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    restartTimer(keyName)
                    return@addOnCompleteListener
                }
                fetchRemoteConfigData(keyName)
            }
        } else {
            fetchRemoteConfigData(keyName)
        }
    }

    private fun fetchRemoteConfigData(keyName: Array<String?>) {
        if (firebaseRemoteConfig == null) {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings =
                FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build()
            firebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
            firebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)

        }
            firebaseRemoteConfig!!.fetch(1)
                .addOnSuccessListener { com: Void? -> fetchRemoteActivateConfigData(keyName) }
                .addOnFailureListener { e: Exception? -> errorCallBack() }
    }

    private fun fetchRemoteActivateConfigData(keyName: Array<String?>) {
        firebaseRemoteConfig!!.fetchAndActivate().addOnSuccessListener { tas: Boolean? ->
            val map = HashMap<String, String>()
            for (key in keyName) {
                if (!TextUtils.isEmpty(key)) {
                    val value = firebaseRemoteConfig!!.getString(key!!)
                    map[key] = value
                }
            }
            if (callBack != null) {
                callBack!!.onRemoteConfigResponse(map)
            }
        }.addOnFailureListener { e: Exception? -> errorCallBack() }
    }

    private fun restartTimer(keyName: Array<String?>) {
        if (FCM_TRIGGER_COUNT == 1) {
            FCM_TRIGGER_COUNT = 2
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (this != null) {
                        triggerFCM(keyName)
                    }
                }
            }, 5000)
        } else {
            errorCallBack()
        }
    }

    private fun errorCallBack() {
        if (callBack != null) {
            callBack!!.onRemoteConfigResponse(null)
        }
    }

    companion object {
        private var fcmConfigHelper: FCMConfigHelper? = null
        val instance: FCMConfigHelper?
            get() {
                if (fcmConfigHelper == null) {
                    fcmConfigHelper = FCMConfigHelper()
                }
                return fcmConfigHelper
            }
    }
}