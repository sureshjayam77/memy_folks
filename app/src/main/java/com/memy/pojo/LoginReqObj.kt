package com.memy.pojo

import com.squareup.moshi.Json

data class LoginReqObj(

	@Json(name="apikey")
	val apikey: String? = "xyz",

	@Json(name="mobile")
	var mobile: String? = null,

	@Json(name="otp")
	var otp: String? = null,

	@Json(name="country_code")
	var country_code: String? = null,

	@Json(name="auto_verify")
	var auto_verify: Boolean? = true,

	@Json(name="key")
	var key: String? = ""

){

}
