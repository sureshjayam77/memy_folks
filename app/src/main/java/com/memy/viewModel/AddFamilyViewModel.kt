package com.memy.viewModel

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.memy.pojo.*
import com.memy.retrofit.AddFamilyRepository
import com.memy.retrofit.DashboardRepository
import com.memy.utils.Constents
import java.io.File

class AddFamilyViewModel : AppBaseViewModel() {
    var moreInfoClicked = MutableLiveData<Boolean>()
    var photoFileUri: Uri? = null
    var familyTagList: List<RelationShipObj> = ArrayList()
    var addFamilyMemberId: MutableLiveData<Int> = MutableLiveData<Int>()
    var showMoreOnfoOption: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isForAddFamily: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isForEditFamily: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isForNewOwnProfileUpdate: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isForOwnProfileUpdate: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var allowEditMobileNumber: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isForPrimaryCountryCode: Boolean? = false
    var addFamilyRepository: AddFamilyRepository
    var relationShipResObj = MutableLiveData<RelationShipResObj>()
    var avatarImageRes = MutableLiveData<AvatarImageListRes>()
    var isForInviteFamilyMember: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    var familyTagName: MutableLiveData<String> = MutableLiveData()
    var familyTagId: MutableLiveData<String> = MutableLiveData()
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var lineageName: MutableLiveData<String> = MutableLiveData()
    var villageName: MutableLiveData<String> = MutableLiveData()
    var inviteSendSMS: MutableLiveData<Boolean> = MutableLiveData()
    var canShowInviteCheckBox: MutableLiveData<Boolean> = MutableLiveData()
    var mainCountryCode: MutableLiveData<String> = MutableLiveData()
    var mainMobileNumber: MutableLiveData<String> = MutableLiveData()
    var altCountryCode: MutableLiveData<String> = MutableLiveData()
    var altMobileNumber: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var gender: MutableLiveData<String> = MutableLiveData()
    var dob: MutableLiveData<String> = MutableLiveData()
    var birthYear: MutableLiveData<String> = MutableLiveData()
    var profession: MutableLiveData<String> = MutableLiveData()
    var living: MutableLiveData<Boolean> = MutableLiveData()
    var deathDateStr: MutableLiveData<String> = MutableLiveData()
    var deathDateStrMap: MutableLiveData<String> = MutableLiveData()
    var popularlyKnowAs: MutableLiveData<String> = MutableLiveData()
    var crazy: MutableLiveData<String> = MutableLiveData()
    var address: MutableLiveData<String> = MutableLiveData()
    var location: MutableLiveData<String> = MutableLiveData()
    var state: MutableLiveData<String> = MutableLiveData()
    var country: MutableLiveData<String> = MutableLiveData()
    var isMale: MutableLiveData<Boolean> = MutableLiveData()
    var isFeMale: MutableLiveData<Boolean> = MutableLiveData()
    var isOtherGender: MutableLiveData<Boolean> = MutableLiveData()
    var profileBase64: MutableLiveData<String> = MutableLiveData()
    var profileUpdateRes = MutableLiveData<ProfileVerificationResObj>()
    var isCusExistRes = MutableLiveData<ProfileVerificationResObj>()
    var stateId = -1
    var countryId = -1

    var countryList: List<SpinnerItem> = ArrayList()
    var stateList: List<SpinnerItem> = ArrayList()
    var countryListRes = MutableLiveData<CountryListRes>()
    var stateListResponse = MutableLiveData<StateListRes>()
    var dashboardRepository: DashboardRepository
    var profileVerificationResObj = MutableLiveData<ProfileVerificationResObj>()
    var userData: ProfileData? = null
    var updatedImageURI : Uri? = null
    var addFamilyRes = MutableLiveData<CommonResponse>()
    var addFamilyMemberRes = MutableLiveData<AddFamilyResponse>()
    var isAddFamilyClicked = true
    var deathDate : String? = null
    var deleteAccountRes = MutableLiveData<CommonResponse>()
    var relationUpdateSuccessRes = MutableLiveData<CommonResponse>()
    var relationShipExistsRes = MutableLiveData<RelationShipExistsRes>()
    var addFamilyReq = AddFamilyRequest()
    var addFamilyFileReq:File? = null
    var selectedProfileURL: String? = ""


    var showRelationPopup: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var inviteCommonResData = MutableLiveData<CommonResponse>()

    init {
        addFamilyRepository = AddFamilyRepository()
        dashboardRepository = DashboardRepository()
        relationShipResObj = addFamilyRepository.relationShipResObj
        countryListRes = addFamilyRepository.countryListRes
        stateListResponse = addFamilyRepository.stateListResponse
        avatarImageRes = addFamilyRepository.avatarImageRes
        profileVerificationResObj = dashboardRepository.profileVerificationResObj
        profileUpdateRes = addFamilyRepository.profileUpdateRes
        addFamilyRes = addFamilyRepository.addFamilyRes
        addFamilyMemberRes = addFamilyRepository.addFamilyMemberRes
        deleteAccountRes = addFamilyRepository.deleteAccountRes
        isCusExistRes = addFamilyRepository.isCusExistRes
        relationUpdateSuccessRes = addFamilyRepository.relationUpdateSuccessRes
        relationShipExistsRes = addFamilyRepository.relationShipExistsRes
        inviteCommonResData = dashboardRepository.inviteCommonResData
        moreInfoClicked.value = false
        isForPrimaryCountryCode = false
        isForAddFamily.value = false
        showMoreOnfoOption.value = false
        allowEditMobileNumber.value = false
        gender.value = Constents.GENDER_MALE
        isMale.value = true
        living.value = true
        isOtherGender.value = false
        addFamilyMemberId.value = -1
        familyTagId.value = ""
        mainCountryCode.value = "+91"
        isForInviteFamilyMember.value = false
    }

    fun moreInfoClicked() {
        moreInfoClicked.value = if (moreInfoClicked.value == true) (false) else (true)
    }

    fun fetchRelationShip() {
        addFamilyRepository.fetchRelationShip()
    }

    fun fetchCountryList() {
        addFamilyRepository.fetchCountry()
    }

    fun fetchStateList() {
        addFamilyRepository.fetchStateList(countryId)
    }

    fun saveFamilyDetails(req: AddFamilyRequest,file: File?) {
        addFamilyRepository.saveProfileDetails(req,file)
    }

    fun checkFamilyMemberExists(req: AddFamilyRequest,file:File?) {
        addFamilyReq = req
        addFamilyFileReq = file
        addFamilyRepository.checkFamilyMemberExists(req.firstname,req.relationship,req.userid)
    }

    fun addFamilyMember() {
        addFamilyRepository.addFamilyCall(addFamilyReq,addFamilyFileReq)
    }

    fun fetchProfile(userId : Int?){
        dashboardRepository.fetchUserProfile(userId)
    }


    fun fetchProfile(userId : Int?,ownerId : Int?){
        dashboardRepository.fetchUserProfile(userId,ownerId)
    }

    fun fetchAvatarImageList() {
        addFamilyRepository.fetchAvatarImages()
    }

    fun updateFieldDataFromUserData(data: ProfileData?,loginUserData: ProfileData?) {
        firstName.value = data?.firstname
        lastName.value = data?.lastname
        if (data?.gender == Constents.GENDER_FEMALE) {
            isFeMale.value = true
            isMale.value = false
            isOtherGender.value = false
        }else if (data?.gender == Constents.GENDER_OTHER) {
            isFeMale.value = false
            isMale.value = false
            isOtherGender.value = true
        } else {
            isFeMale.value = false
            isMale.value = true
            isOtherGender.value = false
        }
        var ccMain = data?.country_code ?: "+91"
        mainCountryCode.value = if(TextUtils.isEmpty(ccMain)) ("+91") else(ccMain)
        mainMobileNumber.value = data?.mobile ?: ""
        altCountryCode.value = ""
        altMobileNumber.value = ""
        dob.value = data?.dateofbirth ?: ""
        email.value = data?.email ?: ""
        profession.value = data?.profession ?: ""
        popularlyKnowAs.value = data?.popularlyknownas ?: ""
        address.value = data?.address ?: ""
        villageName.value = data?.native ?: ""
        lineageName.value = data?.lineage ?: ""
        if(data?.state_id != null) {
            stateId = data?.state_id
        }
        if(data?.country_id != null) {
            countryId = data?.country_id
        }
        living.value = data?.isliving ?: true
        deathDateStrMap.value = data?.dateofdeath ?: ""
        deathDate = if(data?.dateofdeath != null) (data?.dateofdeath!!) else null

        if ((data?.altmobiles != null) && (data?.altmobiles.size > 0)) {
            altCountryCode.value = data?.altmobiles?.get(0)?.country_code
            altMobileNumber.value = data?.altmobiles?.get(0)?.mobile
        }

        if((data?.as_relation != null) && (data.mid != loginUserData?.mid) && (!TextUtils.isEmpty(data?.as_relation?.name))){
            familyTagId.value = data?.as_relation?.id.toString()
            familyTagName.value = data?.as_relation?.name
        }

        if((TextUtils.isEmpty(data?.mobile)) || (TextUtils.isEmpty(data?.country_code))){
            allowEditMobileNumber.value = true
        }


        fetchCountryList()
    }

    fun inviteClick(){
        inviteSendSMS.value = if(inviteSendSMS.value == true) (false) else true
    }

    fun livingClick(){
        living.value = if(living.value == true) (false) else true
    }

    fun deleteAccount(id:Int?){
            addFamilyRepository.deleteAccount(id)
    }

    fun hideRelationPopup(){
        showRelationPopup.value = false
        mainMobileNumber.value = ""
    }

    fun callIsCusExits(){
        val cusReq = CommonMobileNumberObj(mainCountryCode.value,mainMobileNumber.value)
        addFamilyRepository.checkCusExist(cusReq)
    }

    fun callChangeRelationShip(loginUserId : Int?,pluseBtnClickUserId : Int?,relationId : String?,relationChangerId : Int?){
        val cusReq = RelationShipUpdateReq(loginUserId,pluseBtnClickUserId,relationId,relationChangerId)
        addFamilyRepository.updateRelationShip(cusReq)
    }

    fun clearBirthYear(){
        birthYear.value = ""
    }

    fun inviteFamilyMember(userId : String?){
        dashboardRepository.inviteFamilyMember(userId)
    }
}