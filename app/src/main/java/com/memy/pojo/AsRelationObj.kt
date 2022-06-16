package com.memy.pojo

import com.google.gson.annotations.SerializedName

data class AsRelationObj(

	@field:SerializedName("owner_id")
	val ownerId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
