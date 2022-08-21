package com.memy

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.memy.MemyApplication
import com.memy.utils.LocaleHelper
import java.util.*

class MemyApplication : Application() {
    private var localeHelper : LocaleHelper? = LocaleHelper()

    companion object {
        var instance: MemyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //if(localeHelper == null) {
            localeHelper = LocaleHelper()
       // }
        localeHelper?.setLocale(this,"ta")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper().setLocale(base!!,"ta"))
       // if(localeHelper!! == null) {
            localeHelper = LocaleHelper()
       // }
        localeHelper?.setLocale(base,"ta")
    }
}