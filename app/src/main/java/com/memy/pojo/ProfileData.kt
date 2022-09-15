package com.memy.pojo

import com.squareup.moshi.Json

data class ProfileData(

	@Json(name="id")
	val id: Int? = null,

	@Json(name="profession")
	val profession: String? = null,

	@Json(name="dateofbirth")
	val dateofbirth: String? = null,

	@Json(name="isliving")
	val isliving: Boolean? = null,

	@Json(name="firstname")
	val firstname: String? = null,

	@Json(name="address")
	val address: String? = null,

	@Json(name="gender")
	val gender: String? = null,

	@Json(name="owner_id")
	val owner_id: Int? = null,

	@Json(name="created")
	val created: String? = null,

	@Json(name="verified")
	val verified: Boolean? = null,

	@Json(name="verifiedat")
	val verifiedat: Any? = null,

	@Json(name="mobile")
	val mobile: String? = null,

	@Json(name="mid")
	val mid: Int? = null,

	@Json(name="altmobiles")
	val altmobiles: List<CommonMobileNumberObj?>? = null,

	@Json(name="lastname")
	val lastname: String? = null,

	@Json(name="zipcode")
	val zipcode: String? = null,

	@Json(name="country_code")
	val country_code: String? = null,

	@Json(name="popularlyknownas")
	val popularlyknownas: String? = null,

	@Json(name="photo")
	val photo: String? = null,

	@Json(name="pettype")
	val pettype: String? = null,

	@Json(name="dateofdeath")
	val dateofdeath: String? = null,

	@Json(name="state_id")
	val state_id: Int? = null,

	@Json(name="email")
	val email: String? = null,

	@Json(name="country_id")
	val country_id: Int? = null,

	/*@Json(name="relation")
	val relation: String? = null,

	@Json(name="relation_id")
	val relation_id: Int? = null,*/

	@Json(name="relations")
	val relations: List<RelationShipObj>? = null,

	@Json(name="as_relation")
	val as_relation: AsRelationObj? = null,

	@Json(name="about_me")
	var about_me: String? = null,
	@Json(name="facebook_link")
	var facebook_link: String? = null,
	@Json(name="instagram_link")
	var instagram_link: String? = null,
	@Json(name="twitter_link")
	var twitter_link: String? = null,
	@Json(name="linkedin_link")
	var linkedin_link: String? = null,

	@Json(name="native")
	var native: String? = null,
	@Json(name="lineage")
	var lineage: String? = null,
)

