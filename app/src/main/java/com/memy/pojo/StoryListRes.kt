package com.memy.pojo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

class StoryListRes(
    @Json(name="data")
    val data: List<StoryFeedData>? = null,

    @Json(name="statusCode")
    val statusCode: Int? = null,

    @Json(name="errorDetails")
    val errorDetails: ErrorDetails? = null
)

class NotificationRes(
    val data: List<NotificationData>? = null,

    @Json(name="statusCode")
    val statusCode: Int? = null,

    @Json(name="errorDetails")
    val errorDetails: ErrorDetails? = null
)

 class NotificationData {
    var name: String? = null
    var photo: String? = null

    var activity = ""
    var linktext: String? = null
    var link: String? = null
    var humandate = ""
}