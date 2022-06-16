package com.memy

import android.app.Application
import com.memy.MemyApplication

class MemyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MemyApplication? = null
    }
}