package com.memy.pojo

import com.squareup.moshi.Json

data class RelationShipResObj(

	@Json(name="data")
	val data: List<RelationShipObj>? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,

	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)