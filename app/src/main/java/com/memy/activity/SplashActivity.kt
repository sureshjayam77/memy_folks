package com.memy.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.memy.R
import com.memy.listener.FirebaseCallBack
import com.memy.pojo.FCMConfigData
import com.memy.utils.Constents
import com.memy.utils.FCMConfigHelper
import com.squareup.moshi.Moshi
import java.util.*
import kotlin.collections.HashMap

class SplashActivity : AppBaseActivity(), FirebaseCallBack {

    val handler: Handler = Handler(Looper.getMainLooper())
    private val MAX_RUNNABLE_TIME_DELAY: Long = 100
    val runnable: Runnable = Runnable {
        navigateToSignPage()
    }

    private val networkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            initFCM()
            networkConnectivityHandlerRemove()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkConnectivityHandler()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
        if(id == R.id.app_update_dialog_id){
            redirectStore()
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
        handler.postDelayed(runnable, MAX_RUNNABLE_TIME_DELAY)
        prefhelper.saveAppUpdateSkip(Date().time)
    }

    private fun networkConnectivityHandler() {
        try {
            var connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallBack)
            } else {
                val networkReq = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
                connectivityManager.registerNetworkCallback(networkReq, networkCallBack)
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun networkConnectivityHandlerRemove() {
        try {
            var connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallBack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRemoteConfigResponse(value: HashMap<String, String>?) {
        if (value != null) {
            var forceUpdateStr = value?.get(Constents.APP_UPDATE_CONFIG)
            if (!TextUtils.isEmpty(forceUpdateStr)) {
                try {
                    var forceUpdate =
                        moshi.adapter(FCMConfigData::class.java).fromJson(forceUpdateStr)
                    prefhelper.saveFCMAppUpdateData(forceUpdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        validateAppUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (handler != null) {
                handler.removeCallbacks(runnable)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        networkConnectivityHandlerRemove()
    }

    private fun initFCM() {
        if(moshi == null) {
            moshi = Moshi.Builder().build()
        }
        FCMConfigHelper.instance?.getFCMRemoteConfigData(arrayOf(Constents.APP_UPDATE_CONFIG), this)
    }

    private fun navigateToSignPage() {
        val profileData = prefhelper.fetchUserData()
        if ((profileData != null) && (profileData.mid != null)) {
            var intent: Intent?
            if ((profileData?.firstname == null) || (TextUtils.isEmpty(profileData?.firstname.trim()))) {
                intent = Intent(this, AddFamilyActivity::class.java)
                intent.putExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
                intent.putExtra(Constents.OWN_NEW_PROFILE_INTENT_TAG, true)
            } else {
                intent = Intent(this, DashboardActivity::class.java)
                if(getIntent() != null) {
                    val deeplink =
                        getIntent().getStringExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK)
                    intent.putExtra(Constents.NOTIFICATION_INTENT_EXTRA_DEEPLINK, deeplink)
                }
            }
            startActivityIntent(intent, false)
        } else {
            startActivityIntent(Intent(this, SignInActivity::class.java), true)
        }
    }

    private fun validateAppUpdate(){
        val forceUpdateData = prefhelper.fetchFCMAppUpdateData()
        if((forceUpdateData != null) && (forceUpdateData.enableUpdateCheck == true)){
            val latestVersion = forceUpdateData.latestVersion
            val minSdkVersion = forceUpdateData.minRequiredVersion

            if((latestVersion != null) && (!TextUtils.isEmpty(latestVersion.trim())) && (minSdkVersion != null) && (!TextUtils.isEmpty(minSdkVersion.trim()))){
                val currentAppVersion = getAppVersion()

                if((!TextUtils.isEmpty(currentAppVersion)) && (latestVersion.toDouble() > currentAppVersion.toDouble())){
                    var enableForeceUpdate = ((!TextUtils.isEmpty(minSdkVersion)) && (minSdkVersion.toDouble() > currentAppVersion.toDouble()))
                    if(enableForeceUpdate){
                        var contentMsg = getString(R.string.force_app_update_content)
                        showAlertDialog(
                            R.id.app_update_dialog_id,
                            contentMsg,
                            getString(R.string.update_label),
                            ""
                        )
                        return
                    }else{
                        var contentMsg = getString(R.string.soft_app_update_content)
                        val lastSkipValue = prefhelper.funFetchAppUpdateSkip()
                        if(lastSkipValue?.equals(0)!!){
                            showAlertDialog(
                                R.id.app_update_dialog_id,
                                contentMsg,
                                getString(R.string.update_label),
                                getString(R.string.skip_label)
                            )
                            return
                        }else{
                            if(isNotToday(lastSkipValue)){
                                showAlertDialog(
                                    R.id.app_update_dialog_id,
                                    contentMsg,
                                    getString(R.string.update_label),
                                    getString(R.string.skip_label)
                                )
                                return
                            }
                        }
                    }
                }
            }
        }
        handler.postDelayed(runnable, MAX_RUNNABLE_TIME_DELAY)
    }

    fun getAppVersion(): String {
        var version = "";
        try {
            val pInfo: PackageInfo =
                getPackageManager().getPackageInfo(getPackageName(), 0)
            version = pInfo.versionName
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return version
    }

    private fun isNotToday(skipTime : Long):Boolean{
        val cal = Calendar.getInstance()
        val needCal = Calendar.getInstance()
        needCal.timeInMillis = skipTime
        val diff = cal.timeInMillis - needCal.timeInMillis
        return diff >= 3600000
    }

    private fun redirectStore() {
        val forceUpdateData = prefhelper.fetchFCMAppUpdateData()
        if ((forceUpdateData != null) && (forceUpdateData.enableUpdateCheck == true)) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(forceUpdateData.storeLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}