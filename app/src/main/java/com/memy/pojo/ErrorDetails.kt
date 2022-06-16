package com.memy.pojo

import com.squareup.moshi.Json

data class ErrorDetails(

	@Json(name="id")
	val id: String? = null,

	@Json(name="message")
	val message: String? = null
)