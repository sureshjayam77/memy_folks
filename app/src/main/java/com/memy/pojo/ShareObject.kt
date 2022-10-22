package com.memy.pojo

import com.squareup.moshi.Json

data class ShareObject(

	@Json(name="firstname")
	val firstname: String? = null,

	@Json(name="tree_members_count")
	val treeMembersCount: Int? = null,

	@Json(name="share_template")
	val share_template: String? = null,

	@Json(name="photo")
	val photo: String? = null,

	@Json(name="id")
	val id: Int? = null
)
