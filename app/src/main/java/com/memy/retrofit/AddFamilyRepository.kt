package com.memy.retrofit

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody

import java.io.File


class AddFamilyRepository : BaseRepository() {

    var relationShipResObj = MutableLiveData<RelationShipResObj>()
    var countryListRes = MutableLiveData<CountryListRes>()
    var stateListResponse = MutableLiveData<StateListRes>()
    var profileUpdateRes = MutableLiveData<ProfileVerificationResObj>()
    var profileSocialLinkUpdateRes = MutableLiveData<CommonResponse>()
    var addFamilyMemberRes = MutableLiveData<AddFamilyResponse>()
    var addFamilyRes = MutableLiveData<CommonResponse>()
    var deleteWallRes = MutableLiveData<CommonResponse>()
    var wallRes = MutableLiveData<WallResult>()
    var commentRes = MutableLiveData<CommentResult>()
    var familyMemRes = MutableLiveData<FamilyMembersResult>()
    var deleteAccountRes = MutableLiveData<CommonResponse>()
    var isCusExistRes = MutableLiveData<ProfileVerificationResObj>()
    var relationUpdateSuccessRes = MutableLiveData<CommonResponse>()
    var avatarImageRes = MutableLiveData<AvatarImageListRes>()
    var relationShipExistsRes = MutableLiveData<RelationShipExistsRes>()

    fun fetchRelationShip() {
        val relationShipCall = retrofit.create(APIInterface::class.java)
            .fetchRelationShip(APP_KEY_VALUE)
        relationShipCall?.enqueue(object : Callback<RelationShipResObj?> {
            override fun onResponse(
                call: Call<RelationShipResObj?>?,
                response: Response<RelationShipResObj?>?
            ) {
                relationShipResObj.value = response?.body()
            }

            override fun onFailure(call: Call<RelationShipResObj?>?, t: Throwable) {
                relationShipResObj.value = RelationShipResObj(null, 0, null)
            }
        })
    }

    fun fetchCountry() {
        val countryCall =
            retrofit.create(APIInterface::class.java).fetchCountry(APP_KEY_VALUE)
        countryCall?.enqueue(object : Callback<CountryListRes?> {
            override fun onResponse(
                call: Call<CountryListRes?>?,
                response: Response<CountryListRes?>?
            ) {
                countryListRes.value = response?.body()
            }

            override fun onFailure(call: Call<CountryListRes?>?, t: Throwable) {
                countryListRes.value = CountryListRes(null, 0, null)
            }
        })
    }

    fun fetchStateList(countryId: Int?) {
        val countryCall = retrofit.create(APIInterface::class.java)
            .fetchState(APP_KEY_VALUE, countryId)
        countryCall?.enqueue(object : Callback<StateListRes?> {
            override fun onResponse(
                call: Call<StateListRes?>?,
                response: Response<StateListRes?>?
            ) {
                stateListResponse.value = response?.body()
            }

            override fun onFailure(call: Call<StateListRes?>?, t: Throwable) {
                stateListResponse.value = StateListRes(null, 0, null)
            }
        })
    }

    fun saveProfileDetails(req: AddFamilyRequest?, file: File?) {
        // var stringMap : HashMap<String?, RequestBody?> = HashMap()
        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        var arrayHashMap: HashMap<String?, List<CommonMobileNumberObj>?> = HashMap()
        if (req != null) {
            val firstname = req.firstname
            val lastname = req.lastname
            val mobile = req.mobile
            val country_code = req.country_code
            val isliving = req.isliving
            val email = req.email
            val dateofbirth = req.dateofbirth
            val dateofdeath = req.dateofdeath
            val profession = req.profession
            val popularlyknownas = req.popularlyknownas
            val address = req.address
            val state_id = req.state_id ?: -1
            val country_id = req.country_id ?: -1
            val gender = req.gender
            val relationship = req.relationship
            val owner = req.owner
            var native = req.native
            var lineage = req.lineage

            if(TextUtils.isEmpty(native)){
                native = ""
            }
            if(TextUtils.isEmpty(lineage)){
                lineage = ""
            }
            if (!TextUtils.isEmpty(firstname))
                stringHashMap["firstname"] = createPartFromString(firstname)
            if (!TextUtils.isEmpty(lastname))
                stringHashMap["lastname"] = createPartFromString(lastname)
            if (!TextUtils.isEmpty(mobile))
                stringHashMap["mobile"] = createPartFromString(mobile)
            if (!TextUtils.isEmpty(country_code))
                stringHashMap["country_code"] = createPartFromString(country_code)
            stringHashMap["isliving"] = createPartFromString("" + isliving)
            if (!TextUtils.isEmpty(email))
                stringHashMap["email"] = createPartFromString(email)
            if (!TextUtils.isEmpty(dateofbirth))
                stringHashMap["dateofbirth"] = createPartFromString(dateofbirth)
            if (!TextUtils.isEmpty(dateofdeath))
                stringHashMap["dateofdeath"] = createPartFromString(dateofdeath)
            if (!TextUtils.isEmpty(profession))
                stringHashMap["profession"] = createPartFromString(profession)
            if (!TextUtils.isEmpty(popularlyknownas))
                stringHashMap["popularlyknownas"] = createPartFromString(popularlyknownas)
            if (!TextUtils.isEmpty(address))
                stringHashMap["address"] = createPartFromString(address)

            if (TextUtils.isEmpty(req.photo_url )){
                req.photo_url = ""
            }


            if(req.lineage != null){
                stringHashMap["lineage"] = createPartFromString(lineage)
            }
            if(req.native != null){
                stringHashMap["native"] = createPartFromString(native)
            }

                stringHashMap["photo_url"] = createPartFromString(req.photo_url )
            stringHashMap["state_id"] = createPartFromString("" + state_id)
            stringHashMap["country_id"] = createPartFromString("" + country_id)

            if (!TextUtils.isEmpty(relationship))
                stringHashMap["relationship"] = createPartFromString(relationship)
            if (!TextUtils.isEmpty(gender))
                stringHashMap["gender"] = createPartFromString(gender)

            if ((req.altmobiles != null) && (req.altmobiles?.size!! > 0)) {
                arrayHashMap["altmobiles"] = req.altmobiles!!
            } else {
                arrayHashMap["altmobiles"] = ArrayList<CommonMobileNumberObj>()
            }
            var photoBody: MultipartBody.Part? = null

            if (file != null) {
                val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
                photoBody = MultipartBody.Part.createFormData("photo", file.name, reqFile)
            }
            var updateProfileCall: Call<ProfileVerificationResObj?>? = retrofit.create(APIInterface::class.java).saveProfileDetails(
                req?.userid,
                APP_KEY_VALUE,
                stringHashMap,
                photoBody,
                arrayHashMap
            )
            updateProfileCall?.enqueue(object : Callback<ProfileVerificationResObj?> {
                override fun onResponse(
                    call: Call<ProfileVerificationResObj?>?,
                    response: Response<ProfileVerificationResObj?>?
                ) {
                    profileUpdateRes.value = response?.body()
                }

                override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                    profileUpdateRes.value = ProfileVerificationResObj(null, 0, null)
                }
            })
        }
    }

    fun addFamilyCall(req: AddFamilyRequest?, file: File?) {

        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        var arrayHashMap: HashMap<String?, List<CommonMobileNumberObj>?> = HashMap()
        if (req != null) {
            val firstname = req.firstname
            val lastname = req.lastname
            val mobile = req.mobile
            val country_code = req.country_code
            val isliving = req.isliving
            val email = req.email
            val dateofbirth = req.dateofbirth
            val dateofdeath = req.dateofdeath
            val profession = req.profession
            val popularlyknownas = req.popularlyknownas
            val address = req.address
            val state_id = req.state_id ?: -1
            val country_id = req.country_id ?: -1
            val gender = req.gender
            val relationship = req.relationship
            val owner = req.owner
            val userId = req.userid
            var native = req.native
            var lineage = req.lineage

            if(TextUtils.isEmpty(native)){
                native = ""
            }
            if(TextUtils.isEmpty(lineage)){
                lineage = ""
            }

            if (!TextUtils.isEmpty(firstname))
                stringHashMap["firstname"] = createPartFromString(firstname)
            if (!TextUtils.isEmpty(lastname))
                stringHashMap["lastname"] = createPartFromString(lastname)
            if (!TextUtils.isEmpty(mobile))
                stringHashMap["mobile"] = createPartFromString(mobile)
            if (!TextUtils.isEmpty(country_code))
                stringHashMap["country_code"] = createPartFromString(country_code)
            stringHashMap["isliving"] = createPartFromString("" + isliving)
            if (!TextUtils.isEmpty(email))
                stringHashMap["email"] = createPartFromString(email)
            if (!TextUtils.isEmpty(dateofbirth))
                stringHashMap["dateofbirth"] = createPartFromString(dateofbirth)
            if (!TextUtils.isEmpty(dateofdeath))
                stringHashMap["dateofdeath"] = createPartFromString(dateofdeath)
            if (!TextUtils.isEmpty(profession))
                stringHashMap["profession"] = createPartFromString(profession)
            if (!TextUtils.isEmpty(popularlyknownas))
                stringHashMap["popularlyknownas"] = createPartFromString(popularlyknownas)
            if (!TextUtils.isEmpty(address))
                stringHashMap["address"] = createPartFromString(address)
            stringHashMap["state_id"] = createPartFromString("" + state_id)
            stringHashMap["country_id"] = createPartFromString("" + country_id)
            if (TextUtils.isEmpty(req.photo_url )){
                req.photo_url = ""
            }
                stringHashMap["photo_url"] = createPartFromString(req.photo_url )
            if (!TextUtils.isEmpty(relationship))
                stringHashMap["relationship"] = createPartFromString(relationship)
            if (!TextUtils.isEmpty(gender))
                stringHashMap["gender"] = createPartFromString(gender)

            if ((req.altmobiles != null) && (req.altmobiles?.size!! > 0)) {
                arrayHashMap["altmobiles"] = req.altmobiles!!
            } else {
                arrayHashMap["altmobiles"] = ArrayList<CommonMobileNumberObj>()
            }

            if(req.is_send_sms != null){
                stringHashMap["is_send_sms"] = createPartFromString("" + req.is_send_sms)
            }
            if(req.lineage != null){
                stringHashMap["lineage"] = createPartFromString(lineage)
            }
            if(req.native != null){
                stringHashMap["native"] = createPartFromString(native)
            }

            var photoBody: MultipartBody.Part? = null

            if (file != null) {
                val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
                photoBody = MultipartBody.Part.createFormData("photo", file.name, reqFile)
            }

            var addFamilyCall: Call<AddFamilyResponse?>? =
                retrofit.create(APIInterface::class.java).addFamilyData(
                    APP_KEY_VALUE, owner,userId,
                    stringHashMap,
                    arrayHashMap,
                    photoBody
                )

            addFamilyCall?.enqueue(object : Callback<AddFamilyResponse?> {
                override fun onResponse(
                    call: Call<AddFamilyResponse?>?,
                    response: Response<AddFamilyResponse?>?
                ) {
                    addFamilyMemberRes.value = response?.body()
                }

                override fun onFailure(call: Call<AddFamilyResponse?>?, t: Throwable) {
                    addFamilyMemberRes.value = AddFamilyResponse(null, 0, null)
                }
            })
        }
    }

    fun deleteAccount(req: Int?) {
        val addFamilyCall = retrofit.create(APIInterface::class.java)
            .deleteAccount(req, APP_KEY_VALUE)
        addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                deleteAccountRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                deleteAccountRes.value = CommonResponse(null, 0, null)
            }
        })
    }

    fun checkCusExist(req: CommonMobileNumberObj?) {
        val addFamilyCall = retrofit.create(APIInterface::class.java)
            .checkIsExistingMember(req?.mobile,req?.country_code, APP_KEY_VALUE)
        addFamilyCall?.enqueue(object : Callback<ProfileVerificationResObj?> {
            override fun onResponse(
                call: Call<ProfileVerificationResObj?>?,
                response: Response<ProfileVerificationResObj?>?
            ) {
                isCusExistRes.value = response?.body()
            }

            override fun onFailure(call: Call<ProfileVerificationResObj?>?, t: Throwable) {
                isCusExistRes.value = ProfileVerificationResObj(null, 0, null)
            }
        })
    }

    fun updateRelationShip(req: RelationShipUpdateReq?) {
        val addFamilyCall = retrofit.create(APIInterface::class.java)
            .updateRelation(req, APP_KEY_VALUE)
        addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                relationUpdateSuccessRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                relationUpdateSuccessRes.value = CommonResponse(null, 0, null)
            }
        })
    }
    fun addEvent(req: AddEvent?) {

        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        var arrayHashMap: HashMap<String?, List<CommonMobileNumberObj>?> = HashMap()
        if (req != null) {

            stringHashMap["mid"] = createPartFromString(req.mid)
            stringHashMap["slug"] = createPartFromString(req.slug)
            stringHashMap["event_type"] = createPartFromString(req.event_type)
            stringHashMap["event_start_date"] = createPartFromString(req.event_start_date)
            stringHashMap["event_end_date"] = createPartFromString(req.event_end_date)
            stringHashMap["content"] = createPartFromString(req.content)
            stringHashMap["location"] = createPartFromString(req.location)
            stringHashMap["location_pin"] = createPartFromString(req.location_pin)
            stringHashMap["alignment"] = createPartFromString(req.alignment)
            stringHashMap["host1"] =RequestBody.create(MultipartBody.FORM, req.host)
            stringHashMap["host2"] = RequestBody.create(MultipartBody.FORM, req.host2)
            stringHashMap["media_link"] = RequestBody.create(MultipartBody.FORM, req.driveLink)

            var photoBody: MultipartBody.Part? = null
            var file=req.file
            val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            photoBody = MultipartBody.Part.createFormData("file", file.name, reqFile)

            var addFamilyCall: Call<CommonResponse?>? =
                retrofit.create(APIInterface::class.java).addEventData(
                    APP_KEY_VALUE,
                    stringHashMap,
                    photoBody
                )

            addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>?,
                    response: Response<CommonResponse?>?
                ) {
                    addFamilyRes.value = response?.body()
                }

                override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                    addFamilyRes.value = CommonResponse(null, 0, null)
                }
            })
        }
    }
    fun addStatusEvent(mid:String,file:File,text:String) {

        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()

        stringHashMap["mid"] = createPartFromString(mid)
        var content=text

        stringHashMap["content"] = RequestBody.create(MultipartBody.FORM, content)

        var photoBody: MultipartBody.Part? = null
        val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
        photoBody = MultipartBody.Part.createFormData("file", file.name, reqFile)

        var addFamilyCall: Call<CommonResponse?>? =
            retrofit.create(APIInterface::class.java).addStatusEventData(
                APP_KEY_VALUE,
                stringHashMap,
                photoBody
            )

        addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                addFamilyRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                addFamilyRes.value = CommonResponse(null, 0, null)
            }
        })
    }
    fun addComment(mid:String, file: File?, text:String, cid:String) {

        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()

        stringHashMap["mid"] = createPartFromString(mid)
        stringHashMap["wall_id"] = createPartFromString(cid)
        stringHashMap["comment"] =  RequestBody.create(MultipartBody.FORM, text)

        var photoBody: MultipartBody.Part? = null
        var addFamilyCall: Call<CommonResponse?>? =
            retrofit.create(APIInterface::class.java).addWallComment(
                APP_KEY_VALUE,
                stringHashMap
            )

        if(file!=null){
            val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            photoBody = MultipartBody.Part.createFormData("files", file.name, reqFile)
            val fileList:ArrayList<MultipartBody.Part>?=ArrayList()
            fileList!!.add(photoBody)
            addFamilyCall=
                retrofit.create(APIInterface::class.java).addWallComment(
                    APP_KEY_VALUE,
                    stringHashMap,
                    fileList
                )
        }


        addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                addFamilyRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                addFamilyRes.value = CommonResponse(null, 0, null)
            }
        })
    }
    fun addEventComment(mid:String, file: File?, text:String, cid:String) {

        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()

        stringHashMap["mid"] = createPartFromString(mid)
        stringHashMap["event_id"] = createPartFromString(cid)
        stringHashMap["comment"] =  RequestBody.create(MultipartBody.FORM, text)

        var photoBody: MultipartBody.Part? = null
        var addFamilyCall: Call<CommonResponse?>? =
            retrofit.create(APIInterface::class.java).addEventComment(
                APP_KEY_VALUE,
                stringHashMap
            )

        if(file!=null){
            val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            photoBody = MultipartBody.Part.createFormData("files", file.name, reqFile)
            val fileList:ArrayList<MultipartBody.Part>?=ArrayList()
            fileList!!.add(photoBody)
            addFamilyCall=
                retrofit.create(APIInterface::class.java).addEventComment(
                    APP_KEY_VALUE,
                    stringHashMap,
                    fileList
                )
        }


        addFamilyCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                addFamilyRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                addFamilyRes.value = CommonResponse(null, 0, null)
            }
        })
    }

    fun getWallMedia(mid:String) {

        var addFamilyCall: Call<WallResult?>? =
            retrofit.create(APIInterface::class.java).getWallData(
                APP_KEY_VALUE,
                mid
            )

        addFamilyCall?.enqueue(object : Callback<WallResult?> {
            override fun onResponse(
                call: Call<WallResult?>?,
                response: Response<WallResult?>?
            ) {
                wallRes.value = response?.body()
            }

            override fun onFailure(call: Call<WallResult?>?, t: Throwable) {
                wallRes.value = WallResult(null, null)
            }
        })
    }
    fun getEventCommentList(mid:String) {

        var addFamilyCall: Call<CommentResult?>? =
            retrofit.create(APIInterface::class.java).getEventCommentList(
                APP_KEY_VALUE,
                mid
            )

        addFamilyCall?.enqueue(object : Callback<CommentResult?> {
            override fun onResponse(
                call: Call<CommentResult?>?,
                response: Response<CommentResult?>?
            ) {
                commentRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommentResult?>?, t: Throwable) {
                commentRes.value = CommentResult(null, null)
            }
        })
    }
    fun getCommentList(mid:String) {

        var addFamilyCall: Call<CommentResult?>? =
            retrofit.create(APIInterface::class.java).getCommentList(
                APP_KEY_VALUE,
                mid
            )

        addFamilyCall?.enqueue(object : Callback<CommentResult?> {
            override fun onResponse(
                call: Call<CommentResult?>?,
                response: Response<CommentResult?>?
            ) {
                commentRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommentResult?>?, t: Throwable) {
                commentRes.value = CommentResult(null, null)
            }
        })
    }
    fun getFamilyMembersList(mid:String) {

        var addFamilyCall: Call<FamilyMembersResult?>? =
            retrofit.create(APIInterface::class.java).getFamilyMembersList(
                APP_KEY_VALUE,
                mid
            )

        addFamilyCall?.enqueue(object : Callback<FamilyMembersResult?> {
            override fun onResponse(
                call: Call<FamilyMembersResult?>?,
                response: Response<FamilyMembersResult?>?
            ) {
                familyMemRes.value = response?.body()
            }

            override fun onFailure(call: Call<FamilyMembersResult?>?, t: Throwable) {
                familyMemRes.value = FamilyMembersResult(null, null)
            }
        })
    }

    fun updateSocialMediaLinks(userId : Int?,insta : String?,fb : String?,twi:String?,link : String?,abt:String?){
        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        if (insta != null)
            stringHashMap["instagram_link"] = createPartFromString(insta)
        if (fb != null)
            stringHashMap["facebook_link"] = createPartFromString(fb)
        if (twi != null)
            stringHashMap["twitter_link"] = createPartFromString(twi)
        if (link != null)
            stringHashMap["linkedin_link"] = createPartFromString(link)
        if (abt != null)
            stringHashMap["about_me"] = createPartFromString(abt)

        var updateProfileCall: Call<CommonResponse?>? = retrofit.create(APIInterface::class.java).saveSocialMediaDetails(
            userId,
            APP_KEY_VALUE,
            stringHashMap
        )
        updateProfileCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                profileSocialLinkUpdateRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                profileSocialLinkUpdateRes.value = CommonResponse(null, 0, null)
            }
        })

    }
    fun deleteEvent(id:String,mid:String) {
        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        stringHashMap["id"] = createPartFromString(id)
        stringHashMap["mid"] = createPartFromString(mid)
        val relationShipCall = retrofit.create(APIInterface::class.java)
            .deleteEvent(APP_KEY_VALUE,stringHashMap)
        relationShipCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                deleteWallRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                deleteWallRes.value = CommonResponse(null, 0, null)
            }
        })
    }
    fun deleteWall(id:String,mid:String) {
        var stringHashMap: HashMap<String?, RequestBody?> = HashMap()
        stringHashMap["id"] = createPartFromString(id)
        stringHashMap["mid"] = createPartFromString(mid)

        val relationShipCall = retrofit.create(APIInterface::class.java)
            .deleteWall(APP_KEY_VALUE,stringHashMap)
        relationShipCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                deleteWallRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                deleteWallRes.value = CommonResponse(null, 0, null)
            }
        })
    }

    fun fetchAvatarImages() {
        val relationShipCall = retrofit.create(APIInterface::class.java)
            .getAvatarImageList(BaseRepository.APP_KEY_VALUE)
        relationShipCall?.enqueue(object : Callback<AvatarImageListRes?> {
            override fun onResponse(
                call: Call<AvatarImageListRes?>?,
                response: Response<AvatarImageListRes?>?
            ) {
                avatarImageRes.value = response?.body()
            }

            override fun onFailure(call: Call<AvatarImageListRes?>?, t: Throwable) {
                avatarImageRes.value = AvatarImageListRes(null, 0, null)
            }
        })
    }

    fun checkFamilyMemberExists(fName : String?,relationShipId : String?,mid : Int?) {
        val relationShipCall = retrofit.create(APIInterface::class.java)
            .checkFamilyMemberExists(APP_KEY_VALUE,fName,relationShipId,mid)
        relationShipCall?.enqueue(object : Callback<RelationShipExistsRes?> {
            override fun onResponse(
                call: Call<RelationShipExistsRes?>?,
                response: Response<RelationShipExistsRes?>?
            ) {
                relationShipExistsRes.value = response?.body()
            }

            override fun onFailure(call: Call<RelationShipExistsRes?>?, t: Throwable) {
                relationShipExistsRes.value = RelationShipExistsRes(null, 0, null)
            }
        })
    }
}