package com.memy.pojo

import com.memy.api.BaseRepository
import java.io.File
import java.io.Serializable


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
    var photo_url : String? = null
    var lineage : String? = null
    var native : String? = null
    var is_send_sms : Boolean? = null
}
class AddEvent(var mid:String,var slug:String,var event_type:String,var event_start_date:String,var event_end_date:String,var content:String,var location:String,var location_pin:String,var alignment:String,var file: File?,var host:String,var host2:String,var driveLink:String,var attachment:File?)
class  WallResult(var statusCode:Int?, var data: WallEventResult?)
class WallEventResult(var firstname:String,var photo:String,var mobile:String,var events:List<WallData>,var walls:List<WallData>)
class WallData(var host2:String,var host1:String,var mid_id:String,var id:String,var slug:String,var content:String,var alignment:String,var location:String,var location_pin:String,var media:List<WallMediaData>?,var attachments:List<WallMediaData>?,var startedDate:String,var event_start_date:String,var firstname:String,var photo:String,var host1_details:List<HostData>?,var host2_details:List<HostData>?,var media_link:String):Serializable
class HostData(var firstname: String):Serializable
class WallGroupData(var startedDate:String,var data:List<WallData>)
class WallMediaData(var id:String,var wall_id:String,var media_type:String,var uploaded_at:String,var file:String):Serializable

class  CommentResult(var statusCode:Int?, var data: List<CommentObject>?)
class CommentObject(var id:String,var wall_id:String,var comment:String,var created_at:String,var commenter:CommentUserObject,var files:List<WallMediaData>)
class CommentUserObject(var mid:String,var firstname:String,var photo:String)


class  FamilyMembersResult(var statusCode:Int?, var data: FamilyMembersData?)
class  FamilyMembersData(var Members: List<FamilyMembersObject>?)
class FamilyMembersObject(var firstname: String,var id:String)
