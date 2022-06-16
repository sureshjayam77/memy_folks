package com.memy.pojo

import com.memy.api.BaseRepository


class AddFamilyRequest{

    val apikey = BaseRepository.APP_KEY_VALUE
    var userid : Int? = null
    var firstname : String? = null
    var lastname : String? = null
    var relationship : String? = null
    var mobile : String? = null
    var country_code : String? = null
    /*var ccAlt : String? = null
    var mobileNumberAlt : String? = null*/
    var email : String? = null
    var gender : String? = null
    var dateofbirth : String? = null
    var profession : String? = null
    var dateofdeath : String? = null
    var isliving : Boolean? = null
    var popularlyknownas : String? = null
    var dream : String? = null
    var address : String? = null
    var photobase64 : String? = null
    var state_id : Int? = null
    var country_id : Int? = null
    var owner : Int? = null
    var altmobiles : List<CommonMobileNumberObj>? = null
    var id : Int? = null
}