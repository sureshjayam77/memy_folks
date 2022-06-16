package com.memy.listener

interface FirebaseCallBack {
    fun onRemoteConfigResponse(value :HashMap<String,String>?)
}