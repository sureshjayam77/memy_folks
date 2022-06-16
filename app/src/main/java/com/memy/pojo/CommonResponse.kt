package com.memy.pojo

import com.squareup.moshi.Json

data class CommonResponse(

	@Json(name="data")
	val data: CommonResData? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)