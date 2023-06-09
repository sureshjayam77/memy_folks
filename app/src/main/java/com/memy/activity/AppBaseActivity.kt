package com.memy.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.memy.BuildConfig
import com.memy.R
import com.memy.dialog.CommunicationDialog
import com.memy.listener.DialogClickCallBack
import com.memy.pojo.CommunicationDialogData
import com.memy.utils.AppSignatureHelper
import com.memy.utils.PreferenceHelper
import com.memy.utils.Utils
import com.squareup.moshi.Moshi
import android.app.Activity
import android.app.DownloadManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import com.memy.MainActivity

import android.content.BroadcastReceiver
import com.memy.MemyApplication


open abstract class AppBaseActivity : AppCompatActivity(), DialogClickCallBack {

    protected var width = 0
    protected var height = 0
    private var alertDialog : CommunicationDialog? = null
    protected lateinit var prefhelper : PreferenceHelper

    lateinit var moshi: Moshi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            if(actionBar != null) {
                actionBar?.hide()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        moshi = Moshi.Builder().build()
        val screenSize: Point = Utils.getMeasurementDetail(this)
        prefhelper = PreferenceHelper().getInstance(this)
        width = screenSize.x
        height = screenSize.y
    }

    fun startActivityIntent(intent : Intent, isFinish : Boolean){
        startActivity(intent)
        overridePendingTransition(R.anim.slide_left_anim, R.anim.slide_right_anim)
        if(isFinish){
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.slide_right_anim, R.anim.slide_left_anim)
    }

    fun showAlertDialog(dialogId : Int,msg  : String?,positiveBtn : String?,negativeBtn : String?){
        dismissAlertDialog()
        alertDialog = CommunicationDialog(this, CommunicationDialogData(dialogId,msg,positiveBtn,negativeBtn),this)
        alertDialog?.show()
    }
    fun dismissAlertDialog(){
        if(alertDialog != null){
            alertDialog?.dismiss()
        }
    }

    fun startDial(mobileNumber : String?){
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber))
        startActivity(callIntent)
    }

    fun generateOTPKey():String{
        if(BuildConfig.DEBUG){
            return "UstBLG3+E7a"
        }
        var appSignatureHelper = AppSignatureHelper(this)
        var key = appSignatureHelper.appSignatures
        if((key != null) && (key.size > 0)){
            return key[0]
        }
        return ""
    }


    //method to get the right URL to use in the intent
    fun getFacebookPageURL(context: Context, fbUrl : String): String? {
        val packageManager: PackageManager = context.getPackageManager()
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.orca", 0).versionCode
            if (versionCode >= 3002850) { //newer versions of fb app
                "fb://facewebmodal/f?href=$fbUrl"
            }else{
                fbUrl
            }
        } catch (e: PackageManager.NameNotFoundException) {
            fbUrl //normal web url
        }
    }

    open fun isFBAppInstalled(): Boolean {
        return try {
            applicationContext.packageManager.getApplicationInfo("com.facebook.katana", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    open fun hideKeyboard(activity: Activity) {
        try {
            val imm: InputMethodManager =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view: View? = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}