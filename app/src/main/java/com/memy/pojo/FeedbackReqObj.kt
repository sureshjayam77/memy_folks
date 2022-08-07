package com.memy.pojo

import com.google.gson.annotations.SerializedName

data class FeedbackReqObj(

	@field:SerializedName("author")
	val author: Int? = null,

	@field:SerializedName("subject")
	val subject: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
