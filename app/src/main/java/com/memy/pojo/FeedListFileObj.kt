package com.memy.pojo

import com.google.gson.annotations.SerializedName

data class FeedListFileObj(

	@field:SerializedName("file")
	val file: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
