package com.memy.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.*
import com.memy.retrofit.AddFamilyRepository
import java.io.File

class AddEventViewModel:AppBaseViewModel() {
    var photoFileUri: Uri? = null
    var addFamilyRepository: AddFamilyRepository
    var addFamilyRes = MutableLiveData<CommonResponse>()
    var deleteWallRes = MutableLiveData<CommonResponse>()
    var wallRes = MutableLiveData<WallResult>()
    var commentRes = MutableLiveData<CommentResult>()
    var familyMemRes = MutableLiveData<FamilyMembersResult>()
    init {
        addFamilyRepository = AddFamilyRepository()
        addFamilyRes = addFamilyRepository.addFamilyRes
        deleteWallRes = addFamilyRepository.deleteWallRes
        wallRes = addFamilyRepository.wallRes
        commentRes = addFamilyRepository.commentRes
        familyMemRes = addFamilyRepository.familyMemRes
    }
    fun addStatusEvent(mid:String, file: File, text:String) {
        addFamilyRepository.addStatusEvent(mid,file,text)
    }
    fun addComment(mid:String, file: File?, text:String,cid:String) {
        addFamilyRepository.addComment(mid,file,text,cid)
    }
    fun addEventComment(mid:String, file: File?, text:String,cid:String) {
        addFamilyRepository.addEventComment(mid,file,text,cid)
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
    fun getFamilyMembersList(mid:String) {
        addFamilyRepository.getFamilyMembersList(mid)
    }
    fun getEventCommentList(mid:String) {
        addFamilyRepository.getEventCommentList(mid)
    }
    fun deleteWall(mid:String, id:String) {
        addFamilyRepository.deleteWall(mid,id)
    }
    fun deleteEvent(mid:String, id:String) {
        addFamilyRepository.deleteEvent(mid,id)
    }
}