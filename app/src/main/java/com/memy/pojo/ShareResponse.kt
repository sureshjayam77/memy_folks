package com.memy.pojo

import com.squareup.moshi.Json

data class ShareResponse(

	@Json(name="data")
	val data: ShareObject? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)