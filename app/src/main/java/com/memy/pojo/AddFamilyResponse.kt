package com.memy.pojo

import com.squareup.moshi.Json

data class AddFamilyResponse(

	@Json(name="data")
	val data: AddFamilyResData? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)