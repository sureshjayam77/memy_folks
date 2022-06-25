package com.memy.pojo

import com.squareup.moshi.Json

data class RelationShipUpdateReq(
    @Json(name="userid")
    val userid: Int? = null,
    @Json(name="requester")
    val requester: Int? = null,
    @Json(name="relation")
    val relation: String? = null,
    @Json(name="requesting")
    val requesting: Int? = null
)
