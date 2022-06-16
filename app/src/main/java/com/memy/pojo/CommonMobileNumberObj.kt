package com.memy.pojo

import com.squareup.moshi.Json

data class CommonMobileNumberObj(

	@Json(name="country_code")
	val country_code: String? = null,

	@Json(name="mobile")
	val mobile: String? = null
)
