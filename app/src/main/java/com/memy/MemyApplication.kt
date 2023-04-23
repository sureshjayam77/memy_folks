package com.memy

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.res.Configuration
import com.memy.MemyApplication
import com.memy.utils.LocaleHelper
import java.util.*

class MemyApplication : Application() {
    private var localeHelper : LocaleHelper? = LocaleHelper()
    var manager: DownloadManager? = null

    companion object {
        var instance: MemyApplication? = null
        var downloadFileUniqueId : Long = 0
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //if(localeHelper == null) {
            localeHelper = LocaleHelper()
       // }
        localeHelper?.setLocale(this,"en")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper().setLocale(base!!,"en"))
       // if(localeHelper!! == null) {
            localeHelper = LocaleHelper()
       // }
        localeHelper?.setLocale(base,"en")
    }
}