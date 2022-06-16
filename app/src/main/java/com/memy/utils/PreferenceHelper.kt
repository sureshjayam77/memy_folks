package com.memy.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import android.text.TextUtils
import com.memy.pojo.FCMConfigData
import com.memy.pojo.ProfileData
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter


class PreferenceHelper {
    private val preferenceKeyName = "MEMY_FAMILY_APP"
    private val USER_DATA_KEY = "USER_DATA_KEY"
    private val APP_UPDATE_FCM_DATA = "APP_UPDATE_FCM_DATA"
    private val APP_UPDATE_SKIP_TIME = "APP_UPDATE_SKIP_TIME"

    companion object {
        var commonPreference: SharedPreferences? = null
        var moshi: Moshi? = null
    }

    fun getInstance(ctx:Context) : PreferenceHelper{
        commonPreference = ctx.getApplicationContext().getSharedPreferences(preferenceKeyName, MODE_PRIVATE)
        moshi = Moshi.Builder().build()
        return PreferenceHelper()
    }

    fun saveUserData(profileData : ProfileData?){
        val jsonAdapter: JsonAdapter<ProfileData> =
            moshi?.adapter(ProfileData::class.java)!!
        val strProfileData = jsonAdapter.toJson(profileData)
        saveString(USER_DATA_KEY,strProfileData)
    }

    fun fetchUserData():ProfileData?{
        val strProfileData = fetchString(USER_DATA_KEY)
        if(!TextUtils.isEmpty(strProfileData)) {
            val jsonAdapter: JsonAdapter<ProfileData> =
                moshi?.adapter(ProfileData::class.java)!!

            return jsonAdapter.fromJson(strProfileData)
        }
        return null
    }

    fun saveFCMAppUpdateData(profileData : FCMConfigData?){
        val jsonAdapter: JsonAdapter<FCMConfigData> =
            moshi?.adapter(FCMConfigData::class.java)!!
        val strProfileData = jsonAdapter.toJson(profileData)
        saveString(APP_UPDATE_FCM_DATA,strProfileData)
    }

    fun fetchFCMAppUpdateData():FCMConfigData?{
        val strProfileData = fetchString(APP_UPDATE_FCM_DATA)
        if(!TextUtils.isEmpty(strProfileData)) {
            val jsonAdapter: JsonAdapter<FCMConfigData> =
                moshi?.adapter(FCMConfigData::class.java)!!

            return jsonAdapter.fromJson(strProfileData)
        }
        return null
    }

    fun saveAppUpdateSkip(time : Long){
        saveLong(APP_UPDATE_SKIP_TIME,time)
    }

    fun funFetchAppUpdateSkip():Long?{
        return fetchLong(APP_UPDATE_SKIP_TIME)
    }

    private fun saveString(key : String?, value : String?){
        val editor: SharedPreferences.Editor = commonPreference?.edit()!!
        editor.putString(key,value);
        editor.commit()
    }

    private fun saveLong(key : String?, value : Long){
        val editor: SharedPreferences.Editor = commonPreference?.edit()!!
        editor.putLong(key,value);
        editor.commit()
    }

    private fun fetchLong(key : String?):Long?{
        return commonPreference?.getLong(key,0)
    }

    private fun fetchString(key : String?):String?{
        return commonPreference?.getString(key,"")
    }

    public fun appUpdateSkip(time : Long){

    }

    fun clearPref(){
        val editor: SharedPreferences.Editor = commonPreference?.edit()!!
        editor.clear()
        editor.commit()
    }
}