package com.memy.pojo

import com.squareup.moshi.Json

data class OTPVerificationResObj(

	@Json(name="data")
	val data: OTPProfileObj? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)