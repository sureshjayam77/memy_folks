package com.memy.pojo

import com.memy.api.BaseRepository
import com.squareup.moshi.Json

data class ProfileUpdateReq(
	@Json(name = "apikey")
	val apikey: String? = BaseRepository.APP_KEY_VALUE,
	val profession: String? = null,
	val dateofbirth: String? = null,
	val firstname: String? = null,
	val address: String? = null,
	val gender: String? = null,
	val userid: String? = null,
	val lastname: String? = null,
	val popularlyknownas: String? = null,
	val dream: String? = null,
	val stateId: Int? = null,
	val email: String? = null,
	val countryId: Int? = null
)

