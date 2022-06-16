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
    var profileUpdateRes = MutableLiveData<CommonResponse>()
    var addFamilyRes = MutableLiveData<CommonResponse>()
    var deleteAccountRes = MutableLiveData<CommonResponse>()

    fun fetchRelationShip() {
        val relationShipCall = retrofit.create(APIInterface::class.java)
            .fetchRelationShip(BaseRepository.APP_KEY_VALUE)
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
            retrofit.create(APIInterface::class.java).fetchCountry(BaseRepository.APP_KEY_VALUE)
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
            .fetchState(BaseRepository.APP_KEY_VALUE, countryId)
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
            var updateProfileCall: Call<CommonResponse?>? = retrofit.create(APIInterface::class.java).saveProfileDetails(
                req?.userid,
                BaseRepository.APP_KEY_VALUE,
                stringHashMap,
                photoBody,
                arrayHashMap
            )
            updateProfileCall?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>?,
                    response: Response<CommonResponse?>?
                ) {
                    profileUpdateRes.value = response?.body()
                }

                override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                    profileUpdateRes.value = CommonResponse(null, 0, null)
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

            var addFamilyCall: Call<CommonResponse?>? =
                retrofit.create(APIInterface::class.java).addFamilyData(
                    BaseRepository.APP_KEY_VALUE, owner,userId,
                    stringHashMap,
                    arrayHashMap,
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

    fun deleteAccount(req: Int?) {
        val addFamilyCall = retrofit.create(APIInterface::class.java)
            .deleteAccount(req, BaseRepository.APP_KEY_VALUE)
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
}