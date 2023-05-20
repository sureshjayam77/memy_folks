package com.memy.retrofit

import androidx.lifecycle.MutableLiveData
import com.memy.api.APIInterface
import com.memy.api.BaseRepository
import com.memy.pojo.AddStoryReqObj
import com.memy.pojo.CommonResponse
import com.memy.pojo.StateListRes
import com.memy.pojo.StoryListRes
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import okhttp3.MultipartBody


class StoryRepository : BaseRepository() {

    var addStoryRes = MutableLiveData<CommonResponse>()
    var storyListRes = MutableLiveData<StoryListRes>()


    fun addStoryReq(req : AddStoryReqObj){

        var fileMultiPartList: ArrayList<MultipartBody.Part> = ArrayList()
        for(item in req.files.iterator()){
            val reqFile: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), item)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("files", item.name, reqFile)
            fileMultiPartList.add(body)
        }

        val author: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), ""+req.author)
        val userId: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), ""+req.profile)
        val title: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),req.title)
        val description: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),req.content)
        val publishAs: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),req.publishas)
        val publishStatus: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),""+req.status)

        val relationShipCall = retrofit.create(APIInterface::class.java).addStory(BaseRepository.APP_KEY_VALUE,author,userId,title,description,publishAs,publishStatus,fileMultiPartList)
         relationShipCall?.enqueue(object : Callback<CommonResponse?> {
            override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>?
            ) {
                addStoryRes.value = response?.body()
            }

            override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                addStoryRes.value = CommonResponse(null,0,null)
            }
        })
    }

    fun fetchStory(loginUserId : Int?,familyMemberId : Int,pageNumber : Int?,itemLimit : Int?){
        var relationShipCall : Call<StoryListRes?>?
        if(familyMemberId ==0){
            relationShipCall = retrofit.create(APIInterface::class.java).fetchAllOwnStory(BaseRepository.APP_KEY_VALUE,loginUserId,pageNumber,itemLimit)
        }else{
            relationShipCall = retrofit.create(APIInterface::class.java).fetchAllStory(BaseRepository.APP_KEY_VALUE,loginUserId,familyMemberId,pageNumber,itemLimit)
        }
        relationShipCall?.enqueue(object : Callback<StoryListRes?> {
            override fun onResponse(
                call: Call<StoryListRes?>?,
                response: Response<StoryListRes?>?
            ) {
                storyListRes.value = response?.body()
            }

            override fun onFailure(call: Call<StoryListRes?>?, t: Throwable) {
                storyListRes.value = StoryListRes(null,0,null)
            }
        })
    }
}