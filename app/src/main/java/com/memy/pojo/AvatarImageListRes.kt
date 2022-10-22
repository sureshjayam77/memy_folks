package com.memy.pojo

import com.squareup.moshi.Json

data class AvatarImageListRes(

	@Json(name="data")
	val data: List<DataItem>? = null,

	@Json(name="statusCode")
	val statusCode: Int? = null,
	@Json(name="errorDetails")
	val errorDetails: ErrorDetails? = null
)

data class DataItem(

	@Json(name="name")
	val name: String? = null,

	@Json(name="avatar")
	val avatar: String? = null
)
