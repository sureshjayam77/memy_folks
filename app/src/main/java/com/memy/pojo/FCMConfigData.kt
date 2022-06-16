package com.memy.pojo

import com.google.gson.annotations.SerializedName

data class FCMConfigData(

	@field:SerializedName("latestVersion")
	val latestVersion: String? = null,

	@field:SerializedName("enableUpdateCheck")
	val enableUpdateCheck: Boolean? = null,

	@field:SerializedName("minRequiredVersion")
	val minRequiredVersion: String? = null,

	@field:SerializedName("storeLink")
	val storeLink: String? = null
)
