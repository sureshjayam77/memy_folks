package com.memy.pojo

import com.google.gson.annotations.SerializedName

data class ProfileVerificationResObj(

	@field:SerializedName("data")
	val data: ProfileData? = null,

	@field:SerializedName("statusCode")
	val statusCode: Int? = null,

	@field:SerializedName("errorDetails")
	val errorDetails: ErrorDetails? = null
)