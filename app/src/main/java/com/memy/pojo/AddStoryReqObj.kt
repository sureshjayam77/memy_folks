package com.memy.pojo

import java.io.File

data class AddStoryReqObj(
	val publishas: String,
	val author: Int,
	val profile: Int,
	val files: List<File>,
	val title: String,
	val content: String,
	val status: Int

)

