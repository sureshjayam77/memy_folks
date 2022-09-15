package com.memy.pojo

import com.squareup.moshi.Json

data class RelationShipExistsRes(

	@Json(name="data")
	val data: Boolean? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)