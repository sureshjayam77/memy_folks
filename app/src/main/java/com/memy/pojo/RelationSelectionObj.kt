package com.memy.pojo

import com.squareup.moshi.Json

data class RelationSelectionObj(

	@Json(name="gender")
	val gender: String? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="weight")
	val weight: Int? = null,

	@Json(name="id")
	val id: Int? = null,

	@Json(name="is_applicable")
	val is_applicable: Boolean? = null
)
