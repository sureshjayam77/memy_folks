package com.memy.api

import com.memy.pojo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


interface APIInterface {

    @POST("api/sendotp/")
    fun verifyMobileNumber(@Body loginReqObj: LoginReqObj): Call<MobileNumberVerifyResObj?>?

    @POST("api/verifyotpforcontinue/")
    fun verifyOTP(@Body loginReqObj: LoginReqObj ) : Call<ProfileVerificationResObj?>?

   /* @GET("api/profile/")
    fun fetchProfile(@Query("apikey") apikey: String, @Query("userid") user : Int? ) : Call<ProfileVerificationResObj?>?*/

    @GET("api/v1/member/")
    fun fetchProfile( @Query("apikey") apikey: String,@Query("mid") user : Int?, ) : Call<ProfileVerificationResObj?>?


    @GET("api/v1/member/{userid}/")
    fun fetchProfile( @Path("userid") user : Int?, @Query("apikey") apikey: String,@Query("owner_id") owner_id : Int? ) : Call<ProfileVerificationResObj?>?

    @GET("api/relationships/")
    fun fetchRelationShip(@Query("apikey") apikey: String ) : Call<RelationShipResObj?>?

    @GET("api/countries/")
    fun fetchCountry(@Query("apikey") apikey: String ) : Call<CountryListRes?>?

    @GET("api/states/")
    fun fetchState(@Query("apikey") apikey: String, @Query("country_id") countryId : Int? ) : Call<StateListRes?>?

    /*@POST("api/SaveMemberDetails/")
    fun saveProfileDetails(@Body()  req: AddFamilyRequest?) : Call<CommonResponse?>?*/

    @Multipart
    @PUT("api/v1/member/{id}/")
    fun saveProfileDetails(@Path("id") id: Int?,@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?,@PartMap() partArrayMap :  HashMap<String?, List<CommonMobileNumberObj>?>) : Call<ProfileVerificationResObj?>?

    @Multipart
    @PUT("api/v1/member/{id}/")
    fun saveSocialMediaDetails(@Path("id") id: Int?,@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>) : Call<CommonResponse?>?


    @Multipart
    @PUT("api/v1/member/{id}/")
    fun saveProfileDetails(@Path("id") id: Int?,@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@PartMap() partArrayMap :  HashMap<String?, List<CommonMobileNumberObj>?>) : Call<CommonResponse?>?


    @Multipart
    @POST("api/v1/member/")
    fun addFamilyData(@Query("apikey") apikey: String,@Query("owner_id") ownerId: Int?,@Query("parent_id") relationPersionId: Int?,@PartMap() partMap :  HashMap<String?, RequestBody?>,@PartMap() partArrayMap :  HashMap<String?, List<CommonMobileNumberObj>?>,@Part file: MultipartBody.Part?) : Call<AddFamilyResponse?>?

    /*  @POST("api/familymember/")
      fun addFamilyData(@Body()  req: AddFamilyRequest?) : Call<CommonResponse?>?*/

    @DELETE("api/v1/member/{id}/")
    fun deleteAccount(@Path("id")  req: Int?,@Query("apikey") apikey: String) : Call<CommonResponse?>?

    @Multipart
    @POST("api/v1/post/")
    fun addStory(@Query("apikey") apikey: String, @Part("author")  profileId: RequestBody?, @Part("profile")  userId: RequestBody?, @Part("title")  title: RequestBody?, @Part("content")  desc: RequestBody?, @Part("publishas")  publishas: RequestBody?, @Part("status")  storyStatus: RequestBody?, @Part file: List<MultipartBody.Part>) : Call<CommonResponse?>?

    @GET("api/v1/post/")
    fun fetchAllOwnStory(@Query("apikey") apikey: String,@Query("author") loginUserId: Int?,@Query("page") pageNo: Int?,@Query("limit") limitNo: Int? ) : Call<StoryListRes?>?

    @GET("api/v1/post/")
    fun fetchAllStory(@Query("apikey") apikey: String,@Query("author") loginUserId: Int?,@Query("familyId") familId: Int?,@Query("page") pageNo: Int?,@Query("limit") limitNo: Int? ) : Call<StoryListRes?>?

    @GET("api/v1/notifications/{id}/")
    fun getNotifications(@Path("id") apikey: String,@Query("apikey") loginUserId: String? ) : Call<NotificationRes?>?

    @GET("api/v1/existing_member/")
    fun checkIsExistingMember(@Query("mobile") mobile: String?,@Query("country_code") country_code: String?,@Query("apikey") loginUserId: String?) : Call<ProfileVerificationResObj?>?

    @POST("api/v1/existing_member/")
    fun updateRelation(@Body() req: RelationShipUpdateReq?,@Query("apikey") apikey: String?) : Call<CommonResponse?>?

    @PUT("api/v1/sns_token/")
    fun updateFCMToken(@Body() req: FCMTokenUpdateReq?,@Query("apikey") apikey: String?) : Call<CommonResponse?>?
    @Multipart
    @POST("api/v1/event/")
    fun addEventData(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?) : Call<CommonResponse?>?

    @Multipart
    @POST("api/v1/event/")
    fun addEventData(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?,@Part file1: MultipartBody.Part?) : Call<CommonResponse?>?

    @Multipart
    @PUT("api/v1/event/")
    fun editEvent(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?) : Call<CommonResponse?>?

    @Multipart
    @PUT("api/v1/event/")
    fun editEvent(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?,@Part file1: MultipartBody.Part?) : Call<CommonResponse?>?


    @Multipart
    @PUT("api/v1/event/")
    fun editEvent(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>) : Call<CommonResponse?>?

    @Multipart
    @POST("api/v1/wall/")
    fun addStatusEventData(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: MultipartBody.Part?) : Call<CommonResponse?>?

    @GET("api/v1/walls_events/")
    fun getWallData(@Query("apikey") apikey: String,@Query("mid") mid: String) : Call<WallResult?>?

    @Multipart
    @POST("api/v1/wall_comments/")
    fun addWallComment(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: List<MultipartBody.Part>?) : Call<CommonResponse?>?

    @Multipart
    @POST("api/v1/wall_comments/")
    fun addWallComment(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>) : Call<CommonResponse?>?


    @GET("api/v1/wall_comments/")
    fun getCommentList(@Query("apikey") apikey: String,@Query("wall_id") mid: String) : Call<CommentResult?>?

    @POST("api/v1/feedback/")
    fun submitFeedback(@Body() req: FeedbackReqObj?,@Query("apikey") apikey: String?) : Call<CommonResponse?>?

    @PUT("api/v1/existing_member/")
    fun addFamilyAction(@Query("apikey") apikey: String?,@Query("request_id") request_id: Int?,@Query("action") action: String?) : Call<CommonResponse?>?


    @GET("api/v1/event_comments/")
    fun getEventCommentList(@Query("apikey") apikey: String,@Query("event_id") mid: String) : Call<CommentResult?>?

    @Multipart
    @POST("api/v1/event_comments/")
    fun addEventComment(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>,@Part file: List<MultipartBody.Part>?) : Call<CommonResponse?>?

    @Multipart
    @POST("api/v1/event_comments/")
    fun addEventComment(@Query("apikey") apikey: String,@PartMap() partMap :  HashMap<String?, RequestBody?>) : Call<CommonResponse?>?

    @GET("api/v1/family_members/")
    fun getFamilyMembersList(@Query("apikey") apikey: String,@Query("mid") mid: String) : Call<FamilyMembersResult?>?
    @FormUrlEncoded
    @HTTP(method = "DELETE",path="api/v1/wall/", hasBody = true)
    fun deleteWall(@Query("apikey") apikey: String,@Field("id") id:String) : Call<CommonResponse?>?
    @FormUrlEncoded
    @HTTP(method = "DELETE",path="api/v1/event/", hasBody = true)
    fun deleteEvent(@Query("apikey") apikey: String,@Field("id") id:String) : Call<CommonResponse?>?

    @GET("api/memberrelationships/")
    fun getMemberRelationShip(@Query("apikey") apikey: String,@Query("mid") mid: String?) : Call<MemberRelationShipResData?>?

    @GET("api/v1/avatar/")
    fun getAvatarImageList(@Query("apikey") apikey: String) : Call<AvatarImageListRes>?

    @GET("api/memberexist/")
    fun checkFamilyMemberExists(@Query("apikey") apikey: String,@Query("firstname") firstname: String?,@Query("relationship") relationship: String?,@Query("mid") mid: Int?) : Call<RelationShipExistsRes>?

    @GET("api/member_invite/")
    fun inviteFamilyMember( @Query("apikey") apikey: String,@Query("userid") user : String?) : Call<CommonResponse?>?

    @GET("api/membershare/")
    fun fetchShareData( @Query("apikey") apikey: String,@Query("mid") user : String?) : Call<ShareResponse?>?

    @PUT("api/v1/member_admin_access/")
    fun updateAdminAccess(@Query("apikey") apikey: String,@Query("mid1") mid1: Int?,@Query("mid2") mid2: Int?) : Call<CommonResponse?>?

    @DELETE("api/v1/member_admin_access/")
    fun removeAdminAccess(@Query("apikey") apikey: String,@Query("mid1") mid1: Int?,@Query("mid2") mid2: Int?) : Call<CommonResponse?>?


}