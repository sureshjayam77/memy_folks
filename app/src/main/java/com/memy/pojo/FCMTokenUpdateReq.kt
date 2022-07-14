package com.memy.pojo

import com.squareup.moshi.Json

data class FCMTokenUpdateReq(
    @Json(name="mid")
    val mid: String? = null,

    @Json(name="hashed_token")
    val hashed_token: String? = null){
}