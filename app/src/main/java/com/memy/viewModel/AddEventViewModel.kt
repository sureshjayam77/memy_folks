package com.memy.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.AddEvent
import com.memy.pojo.CommentResult
import com.memy.pojo.CommonResponse
import com.memy.pojo.WallResult
import com.memy.retrofit.AddFamilyRepository
import java.io.File

class AddEventViewModel:AppBaseViewModel() {
    var photoFileUri: Uri? = null
    var addFamilyRepository: AddFamilyRepository
    var addFamilyRes = MutableLiveData<CommonResponse>()
    var wallRes = MutableLiveData<WallResult>()
    var commentRes = MutableLiveData<CommentResult>()
    init {
        addFamilyRepository = AddFamilyRepository()
        addFamilyRes = addFamilyRepository.addFamilyRes
        wallRes = addFamilyRepository.wallRes
        commentRes = addFamilyRepository.commentRes
    }
    fun addStatusEvent(mid:String, file: File, text:String) {
        addFamilyRepository.addStatusEvent(mid,file,text)
    }
    fun addComment(mid:String, file: File?, text:String,cid:String) {
        addFamilyRepository.addComment(mid,file,text,cid)
    }
    fun addEvent(addEvent: AddEvent) {
        addFamilyRepository.addEvent(addEvent)
    }
    fun getWallMedia(mid:String) {
        addFamilyRepository.getWallMedia(mid)
    }
    fun getCommentList(mid:String) {
        addFamilyRepository.getCommentList(mid)
    }
}