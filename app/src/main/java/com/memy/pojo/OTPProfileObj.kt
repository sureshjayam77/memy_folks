package com.memy.pojo

import com.squareup.moshi.Json

data class OTPProfileObj(

	@Json(name="profilephoto")
	val profilephoto: String? = null,

	@Json(name="success")
	val success: Int? = null,

	@Json(name="usermobile")
	val usermobile: String? = null,

	@Json(name="message")
	val message: String? = null,

	@Json(name="userid")
	val userid: Int? = null
)
