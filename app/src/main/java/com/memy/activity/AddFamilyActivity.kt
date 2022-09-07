package com.memy.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chivorn.datetimeoptionspicker.DateTimePickerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.memy.R
import com.memy.adapter.AvatarImageAdapter
import com.memy.adapter.CountryCodeAdapter
import com.memy.adapter.CustomDropDownAdapter
import com.memy.databinding.AddFamilyActivityBinding
import com.memy.databinding.ChoosePhotoDialogBinding
import com.memy.databinding.FamilyTagDialogBinding
import com.memy.databinding.FamilyTagItemLayoutBinding
import com.memy.listener.AdapterListener
import com.memy.listener.CustomDropDownCallBack
import com.memy.pojo.*
import com.memy.utils.Constents
import com.memy.utils.PermissionUtil
import com.memy.utils.Utils
import com.memy.viewModel.AddFamilyViewModel
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.lang.reflect.Method
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddFamilyActivity : AppBaseActivity(), View.OnClickListener, AdapterListener {

    lateinit var binding: AddFamilyActivityBinding
    lateinit var viewModel: AddFamilyViewModel
    lateinit var photoBottomSheet: BottomSheetDialog
    private var countryAdapter: CountryCodeAdapter? = null
    private var countryListDialog: Dialog? = null
    private var countryCodeRecyclerView: RecyclerView? = null
    private var emptyTextView: AppCompatTextView? = null
    private var removeIconImageView: AppCompatImageView? = null
    var familyBottomSheet: BottomSheetDialog? = null
    val REQUEST_IMAGE_CAPTURE: Int = 1001
    var dialogFragment: DialogFragment? = null
    val PERMISSION_SETTINGS_NAVIGATION = 1002
    lateinit var countryListDropDown: CustomDropDownAdapter
    lateinit var stateListDropDown: CustomDropDownAdapter
    var isMediaSelected : Boolean? = null
    val yearList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupViewModel()
        setupObserver()
       // PermissionUtil().initRequestPermissionForCameraContact(this, true)
        initProfileData()
        showProgressBar()
        initYearAdapter()
        viewModel.fetchAvatarImageList()
    }

    override fun onBackPressed() {
        if(binding.progressInclude.progressBarLayout.visibility == View.GONE) {
            if((viewModel.showRelationPopup.value != null) && (viewModel.showRelationPopup.value == true)){
                viewModel.showRelationPopup.value = false
            }else if ((viewModel.isForNewOwnProfileUpdate.value != null) && (viewModel.isForNewOwnProfileUpdate.value == true)) {
                super.onBackPressed()
                finishAffinity()
                System.exit(0)
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun setupViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.add_family_activity)
        binding.lifecycleOwner = this
        binding.familyTagTextView.setOnClickListener(this)
        binding.addFamilyBtnTextView.setOnClickListener(this)
        binding.backIconImageView.setOnClickListener(this)
       // binding.saveBtnTextView.setOnClickListener(this)
        binding.requestBtn.setOnClickListener(this)
        binding.familyTagPopupTextView.setOnClickListener(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(AddFamilyViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.isForOwnProfileUpdate.value =
            intent?.getBooleanExtra(Constents.OWN_PROFILE_INTENT_TAG, false)
        viewModel.isForNewOwnProfileUpdate.value =
            intent?.getBooleanExtra(Constents.OWN_NEW_PROFILE_INTENT_TAG, false)
        viewModel.addFamilyMemberId.value =
            intent?.getIntExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, -1)
        viewModel.familyTagId.value = intent?.getIntExtra(Constents.FAMILY_MEMBER_RELATIONSHIP_ID_INTENT_TAG, -1).toString()
        viewModel.isForAddFamily.value =
            intent?.getBooleanExtra(Constents.FAMILY_MEMBER_INTENT_TAG, false)
        viewModel.gender.value = intent?.getStringExtra(Constents.FAMILY_MEMBER_GENDER_INTENT_TAG) ?: Constents.GENDER_MALE
        if(!TextUtils.isEmpty(viewModel.gender.value)) {
            viewModel.isMale.value = false
            viewModel.isFeMale.value = false
            viewModel.isOtherGender.value = false
            if (viewModel.gender.value.equals(Constents.GENDER_MALE, true)) {
                viewModel.isMale.value = true
            } else if (viewModel.gender.value.equals(Constents.GENDER_FEMALE, true)) {
                viewModel.isFeMale.value = true
            } else if (viewModel.gender.value.equals(Constents.GENDER_OTHER, true)) {
                viewModel.isOtherGender.value = true
            }
        }
        viewModel.isForEditFamily.value = intent?.getBooleanExtra(Constents.FAMILY_MEMBER_EDIT_INTENT_TAG, false)

        if(((viewModel.isForOwnProfileUpdate.value == true) && (viewModel.isForNewOwnProfileUpdate.value == false)) || (viewModel.isForEditFamily.value == true)){
            viewModel.showMoreOnfoOption.value = true
        }
        val fName = intent?.getStringExtra(Constents.FAMILY_MEMBER_FNAME_INTENT_TAG) ?: ""
        val lName = intent?.getStringExtra(Constents.FAMILY_MEMBER_LNAME_INTENT_TAG) ?: ""
        if(!TextUtils.isEmpty(lName)){
            viewModel.lastName.value = lName
        }

        if((viewModel.isForOwnProfileUpdate.value == true) || (viewModel.isForNewOwnProfileUpdate.value == true) || (viewModel.isForEditFamily.value == true)){
            binding.titleTextView.text = fName+" "+getString(R.string.label_edit_profile)
        }else{
            val genderLable = intent?.getStringExtra(Constents.FAMILY_MEMBER_GENDER_NAME_INTENT_TAG)
            binding.titleTextView.text = String.format(getString(R.string.label_adding_relationship),genderLable,fName)
        }
        if((viewModel.isForAddFamily.value == true)){
            viewModel.allowEditMobileNumber.value = true
        }
        if((viewModel.isForEditFamily.value == true)){
           // viewModel.fetchProfile(viewModel.addFamilyMemberId.value,prefhelper?.fetchUserData()?.mid)
            viewModel.fetchProfile(viewModel.addFamilyMemberId.value)
        }
    }

    private fun setupObserver() {
        viewModel.relationShipResObj.observe(this, this::validateRelationShipRes)
        viewModel.countryListRes.observe(this, this::validateCountryListRes)
        viewModel.stateListResponse.observe(this, this::validateStateListRes)
        viewModel.profileUpdateRes.observe(this, this::validateProfileUpdateRes)
        viewModel.addFamilyMemberRes.observe(this, this::validateAddFamilyRes)
        viewModel.relationUpdateSuccessRes.observe(this, this::validateRelationUpdateRes)

        viewModel.profileVerificationResObj.observe(this, this::validateProfileRes)
        viewModel.deleteAccountRes.observe(this,this::validateDeleteAccountRes)
        viewModel.isCusExistRes.observe(this,this::validateExisCusRes)
        viewModel.relationShipResObj.observe(this, this::validateRelationShipRes)
        viewModel.avatarImageRes.observe(this, this::loadAvatarImages)
        viewModel.relationShipExistsRes.observe(this, this::validateRelationShipExist)
        viewModel.living.observe(this, Observer { v ->
            if(v) {
                binding.deathDateTextView.text = ""
                viewModel.deathDate = null
                viewModel.deathDateStr.value = ""
            }
        })
        viewModel.deathDateStrMap.observe(this, {str ->
            if(!TextUtils.isEmpty(str)){
                viewModel.deathDateStr.value = getString(R.string.label_death)+":"+str
            }
        })
        viewModel.mainMobileNumber.observe(this,this::validateMobileNumber)
        viewModel.mainCountryCode.observe(this,this::validateMobileNumber)
    }

    private fun initProfileData() {
        viewModel.fetchCountryList()
        if ((viewModel.isForOwnProfileUpdate.value != null) && (viewModel.isForOwnProfileUpdate.value == true)) {
           if((viewModel.addFamilyMemberId.value == prefhelper.fetchUserData()?.mid)) {
               viewModel.userData = prefhelper.fetchUserData()
               viewModel.updateFieldDataFromUserData(viewModel.userData,prefhelper.fetchUserData())
               loadProfileImage(viewModel.userData?.photo)
           }else{
              // viewModel.fetchProfile(viewModel.addFamilyMemberId.value,prefhelper?.fetchUserData()?.mid)
               viewModel.fetchProfile(viewModel.addFamilyMemberId.value)
           }
        } else {
          //  viewModel.fetchRelationShip()
        }
    }

    private fun openContactPicker(){
        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(pickContact)
    }

    @SuppressLint("Range")
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if ((result.resultCode == Activity.RESULT_OK) && (result?.data != null)) {
            val contactUri: Uri = result?.data?.data!!
            if(contactUri != null) {
                val cursor: Cursor? = this.contentResolver
                    .query(contactUri, null, null, null, null)
                try {
                    if (cursor?.getCount() != 0) {
                        var name = ""
                        var mobileNumber = ""
                        var emailId = ""
                        cursor?.moveToFirst()
                        val id = cursor?.getString(cursor?.getColumnIndex(ContactsContract.Contacts._ID))
                        name = cursor?.getString(cursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))!!
                        if (cursor?.getString(cursor?.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))?.toInt()!! > 0
                        ) {
                            val pCur = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                arrayOf(id),
                                null
                            )
                            if(pCur != null) {
                                while (pCur!!.moveToNext()) {
                                    mobileNumber =
                                        pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))!!
                                }
                                pCur?.close()
                            }
                        }

                        val emailCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        if(emailCur != null){
                        while (emailCur!!.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            emailId = emailCur?.getString(
                                emailCur?.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))!!
                        }
                        emailCur?.close()
                        }
                       // if((TextUtils.isEmpty(viewModel.firstName.value)) || ((TextUtils.isEmpty(viewModel.firstName.value?.trim())))){
                        if(!TextUtils.isEmpty(name)) {
                            val nameArray = name.split("\\s".toRegex()).toTypedArray()
                            viewModel.firstName.value = nameArray[0]
                            if(nameArray.size > 1){
                                viewModel.lastName.value = nameArray[1]
                            }else{
                                viewModel.lastName.value = ""
                            }
                        }else{
                            viewModel.firstName.value = ""
                            viewModel.lastName.value = ""
                        }
                     //   }
                        if(!TextUtils.isEmpty(mobileNumber)) {
                           // viewModel.mainCountryCode.value = "+91"
                            viewModel.mainMobileNumber.value =
                                if (mobileNumber.length >= 10) mobileNumber.substring(
                                    mobileNumber.length - 10
                                ) else mobileNumber
                        }else{
                           // viewModel.mainCountryCode.value = ""
                            viewModel.mainMobileNumber.value = ""
                        }
                        if(!TextUtils.isEmpty(emailId)) {
                            viewModel.email.value = emailId
                        }else{
                            viewModel.email.value = ""
                        }
                    }
                } finally {
                    cursor?.close()
                }
            }
        }
    }

    private fun openChoosePhoto() {
        photoBottomSheet = BottomSheetDialog(this, R.style.bottomSheetDialogTheme)
        val choosePhotoDialogBinding: ChoosePhotoDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.choose_photo_dialog, null, false
        )
        choosePhotoDialogBinding.cameraTextView.setOnClickListener(this)
        choosePhotoDialogBinding.galleryTextView.setOnClickListener(this)
        choosePhotoDialogBinding.cancelTextView.setOnClickListener(this)
        photoBottomSheet.setContentView(choosePhotoDialogBinding.root)
        photoBottomSheet.show()
    }

    fun photoChooseOption(view: View) {
        isMediaSelected = true
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            openChoosePhoto()
        }/* else if (PermissionUtil().isCameraStorageContactPermissionUnderDontAsk(this)) {
            showPermissionDialog()
        }*/ else {
            PermissionUtil().requestPermissionForCamera(this, true)
        }
    }

    fun contactChooseOption(view: View) {
        isMediaSelected = false
        if (PermissionUtil().requestPermissionForContact(this, false)) {
            openContactPicker()
        }/* else if (PermissionUtil().isCameraStorageContactPermissionUnderDontAsk(this)) {
            showPermissionDialog()
        }*/ else {
            PermissionUtil().requestPermissionForContact(this, true)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancelTextView -> {
                dismissPhotoBottomSheet()
            }
            R.id.cameraTextView -> {
                dismissPhotoBottomSheet()
                dispatchTakePictureIntent()
            }
            R.id.galleryTextView -> {
                dismissPhotoBottomSheet()
                dispatchImagePickerIntent()
            }
            R.id.familyTagPopupTextView -> {
                openFamilyTag()
            }
            R.id.familyTagTextView -> {
                openFamilyTag()
            }
            R.id.cancelFamilyTagTextView -> {
                dismissFamilyTagBottomSheet()
            }
            R.id.labelTextView -> {
                dismissFamilyTagBottomSheet()
                val textView = (v as AppCompatTextView)
                viewModel.familyTagName.value = textView?.text?.toString()
                viewModel.familyTagId.value = textView?.tag.toString()
                if(viewModel.familyTagList != null) {
                    for (i in viewModel.familyTagList) {
                        if((i != null) && (i.id == (viewModel.familyTagId.value)?.toInt())){
                            if(i.gender.equals(Constents.GENDER_MALE,true)){
                                viewModel.isMale.value = true
                            }else if(i.gender.equals(Constents.GENDER_FEMALE,true)){
                                viewModel.isFeMale.value = true
                            }else if(i.gender.equals(Constents.GENDER_OTHER,true)){
                                viewModel.isOtherGender.value = true
                            }
                            break
                        }
                    }
                }
            }
            R.id.addFamilyBtnTextView -> {
                viewModel.isAddFamilyClicked = true
                validateProfileSubmit()
            }
           /* R.id.saveBtnTextView -> {
                viewModel.isAddFamilyClicked = false
                validateProfileSubmit()
            }*/
            R.id.backIconImageView -> {
                onBackPressed()
            }
            R.id.requestBtn -> {
                updateRelationShipReq()
            }
        }
    }

    private fun dismissPhotoBottomSheet() {
        if (photoBottomSheet != null) {
            photoBottomSheet.dismiss()
        }
    }

    private fun dispatchImagePickerIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_IMAGE_CAPTURE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            viewModel.photoFileUri = getFileURL()
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoFileUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(isMediaSelected != null) {
            if (isMediaSelected == true) {
                if (PermissionUtil().requestPermissionForCamera(this, false)) {
                    openChoosePhoto()
                }else if (PermissionUtil().isCameraStoragePermissionUnderDontAsk(this)) {
                    showPermissionDialog()
                }
            } else {
                if (PermissionUtil().requestPermissionForContact(this, false)) {
                    contactChooseOption(binding.phoneContactIcon)
                }else if (PermissionUtil().isContactPermissionUnderDontAsk(this)) {
                    showPermissionDialog()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (intent != null) {
                viewModel.photoFileUri = intent.data!!
                viewModel.updatedImageURI = intent.data!!
            }
            loadProfileImageFromURI(viewModel.photoFileUri)
           // binding.profilePhotoImageView.setImageURI(viewModel.photoFileUri)
            CropImage.activity(viewModel.photoFileUri).setAspectRatio(1, 1).start(this)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode != RESULT_OK) {
            viewModel.photoFileUri = null
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(intent);
            if (resultCode == RESULT_OK) {
                viewModel.photoFileUri = result?.getUri()!!
                viewModel.updatedImageURI = result?.getUri()!!
                loadProfileImageFromURI(viewModel.updatedImageURI)
                //binding.profilePhotoImageView.setImageURI(viewModel.updatedImageURI)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                viewModel.photoFileUri = null
            }
        }/* else if (requestCode == PERMISSION_SETTINGS_NAVIGATION) {
            if(isMediaSelected != null) {
                if (isMediaSelected == true) {
                    if (PermissionUtil().requestPermissionForCamera(this, false)) {
                        openChoosePhoto()
                    }
                } else {
                    if (PermissionUtil().requestPermissionForContact(this, false)) {
                        contactChooseOption(binding.phoneContactIcon)
                    }
                }
            }
        }*/
        super.onActivityResult(requestCode, resultCode, intent)
    }

    private fun getFileURL(): Uri {
        val applicationContext: Context = applicationContext
        val root: File =
            applicationContext.cacheDir // consider using getExternalFilesDir(Environment.DIRECTORY_PICTURES); you need to check the file_paths.xml
        val timeLng: Long = Date().time
        val capturedPhoto = File(root, "memy_profile_$timeLng.jpeg")
        return FileProvider.getUriForFile(
            applicationContext,
            applicationContext.getPackageName().toString() + ".fileprovider",
            capturedPhoto
        )
    }

    private fun dismissDialog() {
        dialogFragment?.dismiss()
    }

    private fun showPermissionDialog() {
        /*dialogFragment = CommunicationDialog(this,
            CommunicationDialogData(R.id.storage_camera_permission,
                getString(R.string.label_camera_storage_permission_req),
                getString(R.string.label_settings),
                getString(R.string.label_cancel)
            ), this
        );
        (dialogFragment as CommunicationDialog)?.show()*/
        showAlertDialog(
            R.id.storage_camera_permission,
            getString(R.string.label_camera_storage_permission_req),
            getString(R.string.label_settings),
            getString(R.string.label_cancel)
        )
    }

    private fun openFamilyTag() {
        var relationList =  viewModel.familyTagList
        if((viewModel.showRelationPopup.value == true) && (viewModel.isCusExistRes.value != null) && (viewModel.isCusExistRes.value?.data != null) && (viewModel.isCusExistRes.value?.data?.relations != null)){
            relationList = viewModel.isCusExistRes.value?.data?.relations!!
        }
        if ((relationList != null) && (relationList.size > 0)) {
            familyBottomSheet = BottomSheetDialog(this, R.style.bottomSheetDialogTheme)
            val familyTagDialogBinding: FamilyTagDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.family_tag_dialog, null, false
            )
            familyTagDialogBinding.cancelFamilyTagTextView.setOnClickListener(this)
            familyTagDialogBinding.tagLayout.removeAllViews()
            for (i in relationList) {
                val familyTagItemLayoutBinding: FamilyTagItemLayoutBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(this), R.layout.family_tag_item_layout, null, false
                    )
                familyTagItemLayoutBinding.labelTextView.setText(i.name + "")
                familyTagItemLayoutBinding.labelTextView.tag = "" + i.id
                familyTagItemLayoutBinding.labelTextView.setOnClickListener(this)
                familyTagDialogBinding.tagLayout.addView(familyTagItemLayoutBinding.root)
            }
            familyBottomSheet?.setContentView(familyTagDialogBinding.root)
            familyBottomSheet?.show()
        }/* else {
            showProgressBar()
            viewModel.fetchRelationShip()
        }*/
    }

    private fun dismissFamilyTagBottomSheet() {
        familyBottomSheet?.dismiss()
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
        if (id == R.id.storage_camera_permission) {
            navigatePermissionSettingsPage()
        } else if (id == R.id.profile_update_success) {
            if ((viewModel.isForNewOwnProfileUpdate.value != null) && (viewModel.isForNewOwnProfileUpdate.value == true)) {
                startActivityIntent(Intent(this, DashboardActivity::class.java), true)
            } else {
                finish()
            }
        } else if(id == R.id.add_family_success){
            if ((viewModel.isAddFamilyClicked != null) && (viewModel.isAddFamilyClicked == true)) {
                val intent = Intent(this, FamilyMemberProfileActivity::class.java)
                intent.putExtra(Constents.FAMILY_MEMBER_ID_INTENT_TAG, viewModel.addFamilyMemberRes.value?.data?.mid?.toInt())
                startActivityIntent(intent, false)
                finish()
               // startActivityIntent(Intent(this, DashboardActivity::class.java), true)
            } else {
                viewModel.firstName.value = ""
                viewModel.lastName.value = ""
                viewModel.familyTagId.value = ""
                viewModel.familyTagName.value = ""
                viewModel.mainCountryCode.value = "+91"
                viewModel.mainMobileNumber.value = ""
                viewModel.altCountryCode.value = ""
                viewModel.altMobileNumber.value = ""
                viewModel.photoFileUri = null
                viewModel.profileBase64.value = ""
                binding.profilePhotoImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_profile_holder))
            }
        }else if(id == R.id.delete_account_id){
            if((viewModel.userData != null) && (viewModel.userData?.mid != null)) {
                showProgressBar()
                viewModel.deleteAccount(viewModel.userData?.mid!!)
            }
        }else if(id == R.id.delete_account_res_id){
            if(viewModel.userData?.mid == prefhelper.fetchUserData()?.mid){
                prefhelper.clearPref()
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                startActivityIntent(intent, true)
            }else{
                val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                startActivityIntent(intent, true)
            }
        } else if(id == R.id.member_exists_popup_id){
            showProgressBar()
            viewModel.addFamilyMember()
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    private fun navigatePermissionSettingsPage() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent, PERMISSION_SETTINGS_NAVIGATION)
    }

   /* fun openDatePicker(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val minYear = Calendar.getInstance()
        minYear.set(Calendar.YEAR, 1500)

        val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            when (v.id) {
                R.id.deathDateTextView -> {
                    viewModel.deathDateStr.value =
                        (getString(R.string.label_death) + ": " + (if(dayOfMonth > 9) (dayOfMonth) else("0"+dayOfMonth)) + "-" + (if((monthOfYear + 1) > 9) (monthOfYear + 1) else ("0"+(monthOfYear + 1))) + "-" + year)
                    viewModel.deathDate = ""+(if(dayOfMonth > 9) (dayOfMonth) else("0"+dayOfMonth)) + "-" + (if((monthOfYear + 1) > 9) (monthOfYear + 1) else ("0"+(monthOfYear + 1))) + "-" + year
                }
                R.id.dateTextView -> {
                    viewModel.dob.value = "" + (if(dayOfMonth > 9) (dayOfMonth) else("0"+dayOfMonth)) + "-" + (if((monthOfYear + 1) > 9) (monthOfYear + 1) else ("0"+(monthOfYear + 1))) + "-" + year
                }
            }
        }, year, month, day)
        dpd.datePicker.minDate = minYear.time.time
        dpd.datePicker.maxDate = Date().time
        dpd.show()
        dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,R.color.app_color));
        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this,R.color.app_color));
    }*/

    fun openDatePicker(v1: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val minYear = Calendar.getInstance()
        minYear.set(Calendar.YEAR, 1500)

       val dateTimePickerView = DateTimePickerView.Builder(
            this
        ) { date, v -> //Callback
            //yourTextView.setText(NativeDate.getTime(date))
           val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
           val dateValue : String= simpleDateFormat.format(date)
           when (v1.id) {
               R.id.deathDateTextView -> {
                   viewModel.deathDateStr.value = getString(R.string.label_death) + ": " + dateValue
                   viewModel.deathDate = ""+ dateValue
               }
               R.id.dateTextView -> {
                   viewModel.dob.value = "" + dateValue
               }
           }
        }
           .setType(booleanArrayOf(
               true,
               true,
               true,
               false,

               false,
               false
           ))   // year-month-day-hour-min-sec
           .setRange(1500,year)
            .build()
        dateTimePickerView.show()

        }

    fun initYearAdapter() {
        val currentYear = (Calendar.getInstance()).get(Calendar.YEAR)
        val minYear = currentYear - 500
        for(value in minYear..currentYear){
            yearList.add(value.toString())
        }
        yearList.add("")
        yearList.reverse()

        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, yearList)
        binding.yearSpinner.adapter = adapter
        binding.yearSpinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.birthYear.value = yearList.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    /**
     * method used to change the view when click the search button
     * @param searchKey given search key
     */
    private fun searchViewChange(searchKey: kotlin.String) {
        val isEmpty = countryAdapter != null && countryAdapter!!.itemCount === 0
        if (countryCodeRecyclerView != null) {
            countryCodeRecyclerView!!.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
        if (emptyTextView != null) {
            emptyTextView!!.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        removeIconImageView?.setVisibility(if (TextUtils.isEmpty(searchKey)) View.GONE else View.VISIBLE)
    }

    fun countryListDropDown(v: View) {
        when (v.id) {
            R.id.countryCodeTextView -> {
                viewModel.isForPrimaryCountryCode = true
            }
            R.id.altCountryCodeTextView -> {
                viewModel.isForPrimaryCountryCode = false
            }
        }
        countryListDialog = Dialog(this)
        val dialogWinHeight = (height * 0.7432) as Double
        val dialogWinWidth = (width * 0.8) as Double
        val viewHeight = (height * 0.8) as Double
        val viewWidth = (width * 0.9) as Double
        val leftSpace = (width * 0.0416) as Double
        val topSpace = (height * 0.0161) as Double
        val searchLeftSpace = (width * 0.0277) as Double
        if (countryListDialog?.getWindow() != null) {
            countryListDialog?.getWindow()
                ?.setLayout(dialogWinWidth.toInt(), dialogWinHeight.toInt() / 2)
            countryListDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
            countryListDialog?.getWindow()
                ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }
        countryListDialog?.setContentView(R.layout.country_code_drop_down)
        val parentDialogLayout =
            countryListDialog?.findViewById<View>(R.id.parentDialogLayout) as LinearLayout
        val searchViewLayout =
            countryListDialog?.findViewById<View>(R.id.searchViewLayout) as LinearLayout
        val searchViewEditText: AppCompatEditText =
            countryListDialog?.findViewById<View>(R.id.searchViewEditText) as AppCompatEditText
        removeIconImageView =
            countryListDialog?.findViewById<View>(R.id.removeIconImageView) as AppCompatImageView
        emptyTextView =
            countryListDialog?.findViewById<View>(R.id.emptyTextView) as AppCompatTextView
        countryCodeRecyclerView =
            countryListDialog?.findViewById<View>(R.id.countryCodeRecyclerView) as RecyclerView
        emptyTextView?.setVisibility(View.GONE)
        searchViewLayout.setPadding(
            leftSpace.toInt(),
            topSpace.toInt(),
            leftSpace.toInt(),
            topSpace.toInt()
        )
        searchViewEditText.setPadding(searchLeftSpace.toInt(), 0, 0, 0)
        searchViewEditText.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus -> })
        searchViewEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (countryAdapter != null) {
                    countryAdapter?.getFilter()?.filter(cs)
                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })
        val country_code = resources.getStringArray(R.array.countryCode)
        val countryList: ArrayList<CountryListObj> = ArrayList<CountryListObj>()
        for (aCountry_code in country_code) {
            val obj = CountryListObj()
            val countryArray = aCountry_code.split(",").toTypedArray()
            obj.setCountryName(Utils.getCountryName(countryArray[0].trim { it <= ' ' }))
            obj.setCountryNameHint(countryArray[0].trim { it <= ' ' })
            obj.setCountryCode(Integer.valueOf(countryArray[1].trim { it <= ' ' }))
            countryList.add(obj)
        }
        val parentDialogLay = parentDialogLayout.layoutParams as LinearLayout.LayoutParams
        parentDialogLay.height = viewHeight.toInt()
        parentDialogLay.width = viewWidth.toInt()
        parentDialogLayout.layoutParams = parentDialogLay
        countryAdapter = CountryCodeAdapter(this, countryList, this, height, width)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        countryCodeRecyclerView?.setLayoutManager(layoutManager)
        countryCodeRecyclerView?.setAdapter(countryAdapter)
        //countryCodeRecyclerView?.addOnItemTouchListener(RecyclerItemClickListener(this, this::OnItemClickListener))
        countryListDialog?.getWindow()
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        removeIconImageView?.setOnClickListener(View.OnClickListener {
            searchViewEditText.setText("")
            countryCodeRecyclerView?.setAdapter(countryAdapter)
            emptyTextView?.setVisibility(View.GONE)
            countryCodeRecyclerView?.setVisibility(View.VISIBLE)
            removeIconImageView?.setVisibility(View.GONE)
        })
        countryListDialog?.show()
    }

    override fun updateAction(actionCode: Int, data: Any?) {
        if (actionCode == CountryCodeAdapter.SEARCH_DATA_VALUES && data != null) {
            val key = data as kotlin.String
            searchViewChange(key)
        } else if ((actionCode == CountryCodeAdapter.SELECTED_COUNTRY_VALUE) && (data != null)) {
            val code = "+" + java.lang.String.valueOf(data)
            if (viewModel.isForPrimaryCountryCode == true) {
                binding.countryCodeTextView.setText(code)
            } else {
                binding.altCountryCodeTextView.setText(code)
            }
            //binding.mobileNumberEditText.setText("")
            if (countryListDialog != null) {
                countryListDialog?.getWindow()
                    ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                try {
                    val input = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    input.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                /*  binding.countryCodeTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT)
                  binding.mobileNumberEditText.setFocusable(true)
                  binding.mobileNumberEditText.setEnabled(true)
                  binding.mobileNumberEditText.setCursorVisible(true)*/
                countryListDialog?.dismiss()
            }
        }
    }

    private fun validateRelationShipRes(res: RelationShipResObj) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                viewModel.familyTagList = ArrayList()
                if (res.data != null) {
                    viewModel.familyTagList = res.data
                }
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    private fun validateCountryListRes(res: CountryListRes) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if ((viewModel.countryId != null) && (res.data != null) && (res.data.size > 0)) {
                    for (item in res.data) {
                        if (viewModel.countryId == item.id) {
                            viewModel.country.value = item.name
                            viewModel.fetchStateList()
                            break
                        }
                    }
                }
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    private fun validateStateListRes(res: StateListRes) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if ((res.data != null) && (res.data.size > 0) && (viewModel.stateId != null)) {
                    for (item in res.data) {
                        if (viewModel.stateId == item.id) {
                            viewModel.state.value = item.name
                            break
                        }
                    }
                }
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }

    fun  countrySpinnerClick(v: View) {
        if ((viewModel.countryListRes.value != null) && (viewModel.countryListRes.value?.data != null) && (viewModel.countryListRes.value?.data?.size!! > 0)) {
            viewModel.countryList = viewModel.countryListRes.value?.data!!
            countryListDropDown =
                CustomDropDownAdapter(this, viewModel.countryList, object : CustomDropDownCallBack {
                    override fun dropDownItemClick(obj: SpinnerItem?) {
                        viewModel.countryId = obj?.id!!
                        viewModel.country.value = obj?.name
                        hideSpinner(binding.countrySpinner)
                        showProgressBar()
                        viewModel.fetchStateList()
                    }
                })
            binding.countrySpinner.adapter = countryListDropDown
            binding.countrySpinner.performClick()
        } else {
            showProgressBar()
            viewModel.fetchCountryList()
        }
    }

    fun stateSpinnerClick(v: View) {
        if ((viewModel.countryId != null) && (viewModel.countryId != -1)) {
            if ((viewModel.stateListResponse.value != null) && (viewModel.stateListResponse.value?.data != null) && (viewModel.stateListResponse.value?.data?.size!! > 0)) {
                viewModel.stateList = viewModel.stateListResponse.value?.data!!
                stateListDropDown = CustomDropDownAdapter(
                    this,
                    viewModel.stateList,
                    object : CustomDropDownCallBack {
                        override fun dropDownItemClick(obj: SpinnerItem?) {
                            viewModel.state.value = obj?.name
                            viewModel.stateId = obj?.id!!
                            hideSpinner(binding.stateSpinner)
                        }
                    })
                binding.stateSpinner.adapter = stateListDropDown
                binding.stateSpinner.performClick()
            } else {
                showProgressBar()
                viewModel.fetchStateList()
            }
        } else {
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.error_select_country),
                getString(R.string.label_ok),
                ""
            )
        }
    }

    private fun hideSpinner(spinner: Spinner) {
        if (spinner != null) {
            try {
                val method: Method =
                    Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
                method.setAccessible(true)
                method.invoke(spinner)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateProfileSubmit() {
        val familyTag: String? = viewModel.familyTagId.value
        val firstName: String? = viewModel.firstName.value?.trim()
        val lastName: String? = viewModel.lastName.value?.trim()
        var primaryCC: String? = viewModel.mainCountryCode.value?.trim()
        val primaryMobileNumber: String? = viewModel.mainMobileNumber.value?.trim()
        var gender: String? = ""
        if(viewModel.isMale.value == true){
            gender = Constents.GENDER_MALE
        }else if(viewModel.isFeMale.value == true){
            gender = Constents.GENDER_FEMALE
        }else if(viewModel.isOtherGender.value == true){
            gender = Constents.GENDER_OTHER
        }
        if(TextUtils.isEmpty(primaryMobileNumber)){
            primaryCC = ""
            //viewModel.mainCountryCode.value = ""
        }
        val email: String? = viewModel.email.value?.trim()
        var dateOfBirth: String? = viewModel.dob.value?.trim()
        if(TextUtils.isEmpty(dateOfBirth)){
            if((!TextUtils.isEmpty(viewModel.birthYear.value)) && (!TextUtils.isEmpty(viewModel.birthYear.value?.trim()))){
                dateOfBirth = "01-01-"+viewModel.birthYear.value
            }
        }
        val profession: String? = viewModel.profession.value?.trim()
        val popularKnownAs: String? = viewModel.popularlyKnowAs.value?.trim()
        val crazy: String? = viewModel.crazy.value?.trim()
        val countryId: Int? = viewModel.countryId!!
        val stateId: Int? = viewModel.stateId!!
        val address: String? = viewModel.address.value?.trim()
        var altCC: String? = viewModel.altCountryCode.value
        val altMobile: String? = viewModel.altMobileNumber.value
        val living: Boolean? = viewModel.living.value
        val deathDate: String? = if(viewModel.deathDate != null) (viewModel.deathDate?.trim()) else null

        if(TextUtils.isEmpty(altMobile)){
            altCC = ""
            viewModel.altCountryCode.value = ""
        }

        var msg: String = ""
        if ((TextUtils.isEmpty(firstName)) || (firstName?.length!! < 2)) {
            msg = getString(R.string.first_name_error)
        }/* else if ((TextUtils.isEmpty(lastName)) || (firstName?.length!! < 0)) {
            msg = getString(R.string.last_name_error)
        }*/else if ((!TextUtils.isEmpty(primaryMobileNumber)) && (primaryMobileNumber?.length!! > 0) && (primaryMobileNumber?.length!! <= 9)
        )
        {
            msg = getString(R.string.mobile_number_error)
        } else if (((TextUtils.isEmpty(altCC)) && (!TextUtils.isEmpty(altMobile))) || ((!TextUtils.isEmpty(
                altCC
            )) && (TextUtils.isEmpty(altMobile)))
        ) {
            msg = getString(R.string.alternate_mobile_number_error)
        } else if (((!TextUtils.isEmpty(email)) && (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()))
        ) {
            msg = getString(R.string.email_id_error)
        }/* else if (((living == false) && (TextUtils.isEmpty(deathDate)))) {
            msg = getString(R.string.death_date_error)
        }*/
        if((viewModel.isForAddFamily.value != null) && (viewModel.isForAddFamily.value == true)){
            if(TextUtils.isEmpty(familyTag)){
                msg = getString(R.string.select_relation_ship)
            }/*else if (((TextUtils.isEmpty(primaryCC)) && (!TextUtils.isEmpty(primaryMobileNumber))) || ((!TextUtils.isEmpty(
                    primaryCC
                )) && (TextUtils.isEmpty(primaryMobileNumber)))
            )*/
            else if ((!TextUtils.isEmpty(primaryMobileNumber)) && (primaryMobileNumber?.length!! > 0) && (primaryMobileNumber?.length!! <= 9)
            )
             {
                msg = getString(R.string.mobile_number_error)
            }
        }

        if (!TextUtils.isEmpty(msg)) {
            showAlertDialog(R.id.do_nothing, msg, getString(R.string.label_ok), "")
        }else  if (!Utils.isNetworkConnected(this)) {
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.no_internet),
                getString(R.string.close_label),
                ""
            )
        } else {
            val req = AddFamilyRequest()
            req.firstname = firstName
            req.lastname = lastName
            req.email = email
            req.gender = gender
            req.dateofbirth = dateOfBirth
            req.profession = profession
            req.isliving = living
            req.dateofdeath = deathDate
            req.popularlyknownas = popularKnownAs
            req.dream = crazy
            req.address = address

            if ((stateId != null) && (stateId!! > -1)) {
                req.state_id = stateId
            }
            if ((countryId != null) && (countryId!! > -1)) {
                req.country_id = countryId
            }
            var file : File? = null
            if (viewModel.updatedImageURI != null) {
               /* val mineType = Utils.getMimeType(this, viewModel.updatedImageURI)
                val imageStream: InputStream? =
                    contentResolver.openInputStream(viewModel.updatedImageURI!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                req.photobase64 = "data:$mineType;base64," + "" + Utils.encodeImage(selectedImage)*/
                  //  file = File(viewModel.updatedImageURI.toString())
                file = File(URI(viewModel.updatedImageURI.toString()))

            }
            if ((!TextUtils.isEmpty(altCC)) && (!TextUtils.isEmpty(altMobile))) {
                val atlMobileNumberList: ArrayList<CommonMobileNumberObj> = ArrayList()
                atlMobileNumberList.add(CommonMobileNumberObj(altCC, altMobile))
                req.altmobiles = atlMobileNumberList
            }
            if(!TextUtils.isEmpty(viewModel.selectedProfileURL)){
                req.photo =  viewModel.selectedProfileURL
            }
            showProgressBar()
            if(((viewModel.isForAddFamily.value != null) && (viewModel.isForAddFamily.value == true))){
                req.relationship = familyTag
                req.country_code = primaryCC
                req.mobile = primaryMobileNumber
                req.userid = viewModel.addFamilyMemberId?.value
                req.id = viewModel.addFamilyMemberId?.value
                req.owner = prefhelper.fetchUserData()?.mid
                viewModel.checkFamilyMemberExists(req,file)
            }else if(((viewModel.userData?.owner_id == prefhelper.fetchUserData()?.mid!!) && (viewModel.userData?.mid!! != prefhelper.fetchUserData()?.mid!!))){
                req.userid = viewModel.userData?.mid
                req.id = viewModel.userData?.id
                req.country_code = primaryCC
                req.mobile = primaryMobileNumber
                req.owner = prefhelper.fetchUserData()?.mid
                viewModel.saveFamilyDetails(req,file)
            }else {
                req.userid = prefhelper.fetchUserData()?.mid
                req.id = prefhelper.fetchUserData()?.id
                req.owner = prefhelper.fetchUserData()?.mid
                viewModel.saveFamilyDetails(req,file)
            }
        }
    }

    private fun validateProfileRes(res: ProfileVerificationResObj) {
        binding.progressInclude.progressBarLayout.visibility = View.GONE
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null)) {
                val userData: ProfileData? = res.data!!
                viewModel.userData = userData
                loadProfileImage(userData?.photo)
                viewModel.updateFieldDataFromUserData(userData!!,prefhelper.fetchUserData())
            } else {
                var message = ""
                if (res.errorDetails != null) {
                    message = res.errorDetails.message!!
                }
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.something_went_wrong)
                }
                showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
            }
        }
    }


    private fun loadProfileImage(url: String?) {
        if (!TextUtils.isEmpty(url)) {
            viewModel.selectedProfileURL = url
            Glide
                .with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(binding.profilePhotoImageView)
        }
    }

    private fun loadProfileImageFromURI(url: Uri?) {
        if (url != null) {
            viewModel.selectedProfileURL = ""
            Glide
                .with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_profile_male)
                .error(R.drawable.ic_profile_male)
                .into(binding.profilePhotoImageView)
        }
    }

    private fun showProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.GONE

    }

    private fun errorHandler(res: CommonResponse) {
        var message = ""
        if ((res != null) && (res.errorDetails != null)) {
            message = res.errorDetails.message!!
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.something_went_wrong)
        }
        showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
    }

    private fun errorHandler(res: AddFamilyResponse) {
        var message = ""
        if ((res != null) && (res.errorDetails != null)) {
            message = res.errorDetails.message!!
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.something_went_wrong)
        }
        showAlertDialog(R.id.do_nothing, message, getString(R.string.close_label), "")
    }

    private fun validateProfileUpdateRes(res: ProfileVerificationResObj) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    if(res?.data?.mid == prefhelper.fetchUserData()?.mid){
                        prefhelper.saveUserData(res.data)
                    }
                    showAlertDialog(
                        R.id.profile_update_success,
                        getString(R.string.profile_updated_success_fully),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(CommonResponse(null,res.statusCode,res.errorDetails))
                }
            } else {
                errorHandler(CommonResponse(null,res.statusCode,res.errorDetails))
            }
        }
    }

    private fun validateAddFamilyRes(res: AddFamilyResponse) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.add_family_success,
                        getString(R.string.family_member_add_success_fully),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }

    private fun validateRelationUpdateRes(res: CommonResponse) {
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.add_family_success,
                        getString(R.string.relation_updated_success_fully),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }


    private fun validateDeleteAccountRes(res: CommonResponse){
       hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.delete_account_res_id,
                        getString(R.string.account_delete_success),
                        getString(R.string.label_ok),
                        ""
                    )
                } else {
                    errorHandler(res)
                }
            } else {
                errorHandler(res)
            }
        }
    }

    private fun validateExisCusRes(res: ProfileVerificationResObj){
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if ((res?.data != null) && (binding != null) && (prefhelper.fetchUserData()?.mid != res?.data?.mid)) {
                    viewModel.familyTagId.value = ""
                    viewModel.familyTagName.value = ""
                    viewModel.showRelationPopup.value = true
                    if (!TextUtils.isEmpty(res?.data.photo)) {
                        Glide
                            .with(this)
                            .load(res?.data.photo)
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile_male)
                            .error(R.drawable.ic_profile_male)
                            .into(binding.relationPhotoImageView)
                    }else{
                        binding.relationPhotoImageView.setImageResource(R.drawable.ic_profile_male)
                    }
                    binding.relationPersonNameTextView.setText(res?.data.firstname ?: "")
                }
            }
        }
    }

    fun updateRelationShipReq(){
        if(!TextUtils.isEmpty(viewModel.familyTagId.value)){
            if (!Utils.isNetworkConnected(this)) {
                showAlertDialog(
                    R.id.do_nothing,
                    getString(R.string.no_internet),
                    getString(R.string.close_label),
                    ""
                )
            }else{
                showProgressBar()
                viewModel.callChangeRelationShip(prefhelper.fetchUserData()?.mid!!,viewModel.addFamilyMemberId?.value!!,viewModel.familyTagId.value!!,viewModel.isCusExistRes.value?.data?.mid!!)
            }
        }else{
            showAlertDialog(R.id.do_nothing, getString(R.string.select_relation_ship), getString(R.string.close_label), "")
        }
    }



    public fun deleteUser(v:View?){
        showAlertDialog(
            R.id.delete_account_id,
            getString(R.string.delete_account_desc),
            getString(R.string.label_yes),
            getString(R.string.label_cancel)
        )
    }

    public fun cancelActivity(v:View?){
        onBackPressed()
    }

    fun validateMobileNumber(str : String){
        if((viewModel.isForAddFamily.value != null) && (viewModel.isForAddFamily.value == true) && (viewModel.mainMobileNumber.value != null) && (viewModel.mainMobileNumber.value?.length!! > 9)/* && (viewModel.mainCountryCode.value != null) && (viewModel.mainCountryCode.value?.length!! > 1)*/){
            showProgressBar()
            viewModel.callIsCusExits()
        }
    }

    fun showYearPicker(v:View?){
        binding.yearSpinner.performClick()
    }

    fun loadAvatarImages(res: AvatarImageListRes){
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                val adapter = AvatarImageAdapter(this, res?.data, object : AdapterListener {
                    override fun updateAction(actionCode: Int, data: Any?) {
                    loadProfileImage((data as DataItem).avatar)
                    }
                })
                binding.avatarRecyclerview.adapter = adapter
            }
        }
    }

    fun validateRelationShipExist(res : RelationShipExistsRes){
        hideProgressBar()
        if (res != null) {
            if ((res.statusCode == 200) && (res.data != null) && (res.data)) {
                showAlertDialog(
                    R.id.member_exists_popup_id,
                    getString(R.string.member_already_exists_msg),
                    getString(R.string.continue_label),
                    getString(R.string.label_cancel)
                )
                return
            }
        }
        showProgressBar();
        viewModel.addFamilyMember()
    }
}


