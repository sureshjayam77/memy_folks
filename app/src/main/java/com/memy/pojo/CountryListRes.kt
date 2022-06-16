package com.memy.pojo

import com.squareup.moshi.Json

class CountryListRes(
    @Json(name="data")
    val data: List<SpinnerItem>? = null,

    @Json(name="statusCode")
    val statusCode: Int? = null,

    @Json(name="errorDetails")
    val errorDetails: ErrorDetails? = null
) {

}