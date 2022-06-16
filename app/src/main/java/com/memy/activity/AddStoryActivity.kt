package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.memy.R
import com.memy.adapter.AddStoryMediaAdapter
import com.memy.adapter.ItemClickListener
import com.memy.databinding.AddStoryActivityBinding
import com.memy.databinding.StoryMediaBottomSheetBinding
import com.memy.pojo.AddStoryMediaObj
import com.memy.pojo.AddStoryReqObj
import com.memy.pojo.CommonResponse
import com.memy.utils.Constents
import com.memy.utils.PermissionUtil
import com.memy.utils.StoryMediaType
import com.memy.utils.Utils
import com.memy.viewModel.AddStoryViewModel
import java.io.File


class AddStoryActivity : AppBaseActivity(), View.OnClickListener, ItemClickListener {

    lateinit var photoBottomSheet: BottomSheetDialog
    private val SELECT_PICTURE = 9001
    private val SELECT_VIDEO = 9002
    private val SELECT_AUDIO = 9003
    val PERMISSION_SETTINGS_NAVIGATION = 1002
    private lateinit var addStoryMediaAdapter: AddStoryMediaAdapter
    //private var mediaList: ArrayList<AddStoryMediaObj> = ArrayList()
    private lateinit var binding: AddStoryActivityBinding
    private lateinit var viewModel : AddStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
        initRecyclerView()
        initObserver()
        PermissionUtil().initRequestPermissionForCamera(this, true)
    }

    fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.add_story_activity)
        binding.lifecycleOwner = this
    }

    fun initViewModel(){
        viewModel = ViewModelProvider(this).get(AddStoryViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.userData = prefhelper.fetchUserData()
    }

    fun initObserver(){
       viewModel.storyTitle.observe(this, Observer { v ->
           viewModel.validateSubmitBtn()
       })
        viewModel.storyDesc.observe(this, Observer { v ->
            viewModel.validateSubmitBtn()
        })
        viewModel.storyAccess.observe(this, Observer { v ->
            viewModel.validateSubmitBtn()
        })
        viewModel.storyMedia.observe(this, Observer { v ->
            viewModel.validateSubmitBtn()
        })

        viewModel.addStoryRes.observe(this, this::validateAddStoryRes)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancelTextView -> {
                dismissPhotoBottomSheet()
            }
            R.id.photoTextView -> {
                dismissPhotoBottomSheet()
                photoPicker()
            }
            R.id.videoTextView -> {
                dismissPhotoBottomSheet()
                videoPicker()
            }
            R.id.audioTextView -> {
                dismissPhotoBottomSheet()
                audioPicker()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERMISSION_SETTINGS_NAVIGATION) {
            if (PermissionUtil().requestPermissionForCamera(this, false)) {
                openChoosePhoto()
            }
        } else if (((requestCode == SELECT_PICTURE) || (requestCode == SELECT_VIDEO) || (requestCode == SELECT_AUDIO)) && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedUri: Uri = data.getData()!!
                /* val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                 val cursor: Cursor? = contentResolver.query(
                     selectedImage, filePathColumn, null, null, null
                 )
                 cursor?.moveToFirst()
                 val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0])!!
                 val filePath: String = cursor.getString(columnIndex)
                 cursor.close()
                 try{
                     val f = File(selectedImage.path)
                 }catch (e:Exception){
                     e.printStackTrace()
                 }*/
                if (selectedUri != null) {
                    val filePath =
                        Utils.getFilePathFromContentUri(selectedUri, this.contentResolver)
                    val mimeType = contentResolver.getType(selectedUri)
                    var storyMediaType: StoryMediaType = StoryMediaType.IMAGE
                    if ((!TextUtils.isEmpty(mimeType)) && (!TextUtils.isEmpty(mimeType?.trim()))) {
                        if (mimeType?.lowercase()?.contains("image") == true) {
                            storyMediaType = StoryMediaType.IMAGE
                        }else if (mimeType?.lowercase()?.contains("video") == true) {
                            storyMediaType = StoryMediaType.VIDEO
                        }else if (mimeType?.lowercase()?.contains("audio") == true) {
                            storyMediaType = StoryMediaType.AUDIO
                        }
                    }
                    if (!TextUtils.isEmpty(filePath)) {
                        val mediaList = viewModel.storyMedia.value
                        mediaList?.add(AddStoryMediaObj(filePath, selectedUri, storyMediaType))
                        viewModel.storyMedia.value = mediaList
                        addStoryMediaAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openChoosePhoto() {
        photoBottomSheet = BottomSheetDialog(this, R.style.bottomSheetDialogTheme)
        val storyMediaBottomSheetBinding: StoryMediaBottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.story_media_bottom_sheet, null, false
        )
        storyMediaBottomSheetBinding.photoTextView.setOnClickListener(this)
        storyMediaBottomSheetBinding.videoTextView.setOnClickListener(this)
        storyMediaBottomSheetBinding.audioTextView.setOnClickListener(this)
        storyMediaBottomSheetBinding.cancelTextView.setOnClickListener(this)
        photoBottomSheet.setContentView(storyMediaBottomSheetBinding.root)
        photoBottomSheet.show()
    }

    private fun photoPicker() {
//        val i = Intent()
//        i.type = "image/*"
//        i.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)

        val photo_picker_intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(photo_picker_intent, SELECT_PICTURE)
    }

    private fun videoPicker() {
        val video_picker_intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
//        viewModel.photoFileUri = getFileURL()
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoFileUri)
        startActivityForResult(video_picker_intent, SELECT_VIDEO)
    }

    private fun audioPicker() {
        val audio_picker_intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(audio_picker_intent, SELECT_AUDIO)
    }

    fun saveStory(v: View) {

    }

    private fun showPermissionDialog() {
        showAlertDialog(
            R.id.storage_camera_permission,
            getString(R.string.label_camera_storage_permission_req),
            getString(R.string.label_settings),
            getString(R.string.label_cancel)
        )
    }

    override fun dialogPositiveCallBack(id: Int?) {
        dismissAlertDialog()
        if (id == R.id.storage_camera_permission) {
            navigatePermissionSettingsPage()
        } else if (id == R.id.add_story_success_id) {
            var intent = Intent()
            setResult(Activity.RESULT_OK,intent)
            finish()
            //startActivityIntent(Intent(this, DashboardActivity::class.java), true)
        }
    }

    override fun dialogNegativeCallBack() {
        dismissAlertDialog()
    }

    private fun dismissPhotoBottomSheet() {
        if (photoBottomSheet != null) {
            photoBottomSheet.dismiss()
        }
    }

    fun photoChooseOption(view: View) {
        if((viewModel.storyMedia.value != null) && (viewModel.storyMedia.value?.size!! > 3)){
            showAlertDialog(
                R.id.do_nothing,
                getString(R.string.already_added_max_media),
                getString(R.string.label_ok),
                ""
            )
            return
        }
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            openChoosePhoto()
        } else if (PermissionUtil().isCameraStoragePermissionUnderDontAsk(this)) {
            showPermissionDialog()
        } else {
            PermissionUtil().requestPermissionForCamera(this, true)
        }
    }

    fun initRecyclerView() {
        addStoryMediaAdapter = AddStoryMediaAdapter(this, viewModel.storyMedia.value!!)
        binding.selectedMediaRecyclerView.adapter = addStoryMediaAdapter
    }

    fun updatePrivatePost(v: View){
        binding.privateTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
        binding.privateTextView.setBackgroundResource(R.drawable.blue_btn_bg)
        binding.myTreeTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.myTreeTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        binding.publicTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.publicTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        viewModel.storyAccess.value = Constents.STORY_ACCESS_PRIVATE
    }

    fun updateMyTreePost(v: View){
        binding.privateTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.privateTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        binding.myTreeTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
        binding.myTreeTextView.setBackgroundResource(R.drawable.blue_btn_bg)
        binding.publicTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.publicTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        viewModel.storyAccess.value = Constents.STORY_ACCESS_FAMILY
    }

    fun updatePublicPost(v: View){
        binding.privateTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.privateTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        binding.myTreeTextView.setTextColor(ContextCompat.getColor(this,R.color.gray_text_color))
        binding.myTreeTextView.setBackgroundResource(R.drawable.gray_stroke_bg)
        binding.publicTextView.setTextColor(ContextCompat.getColor(this,R.color.white))
        binding.publicTextView.setBackgroundResource(R.drawable.blue_btn_bg)
        viewModel.storyAccess.value = Constents.STORY_ACCESS_PUBLIC
    }

    override fun onPause() {
        super.onPause()
        if(addStoryMediaAdapter != null){
            addStoryMediaAdapter.notifyDataSetChanged()
        }
    }

    fun submitStory(v:View){
        if(viewModel.enableAddStoryBtn.value == true){
            var title = viewModel.storyTitle.value?.trim()!!
            var desc = viewModel.storyDesc.value?.trim()!!
            var storyAccess = viewModel.storyAccess.value!!
            var storyMedia = viewModel.storyMedia.value
            val userId = viewModel.userData?.mid!!
            val fileList = ArrayList<File>()

            if(storyMedia != null) {
                for (item in storyMedia?.iterator()!!) {
                    fileList.add(File(item?.filePath!!))
                }
            }

            val req = AddStoryReqObj(storyAccess,userId,userId,fileList,title, desc,0)
            showProgressBar()
            viewModel.addStoryRepository.addStoryReq(req)
        }
    }

    private fun validateAddStoryRes(res: CommonResponse){
        hideProgressBar()
        if (res != null) {
            if (res.statusCode == 200) {
                if (res?.data != null) {
                    showAlertDialog(
                        R.id.add_story_success_id,
                        getString(R.string.story_added_success_fully),
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

    private fun navigatePermissionSettingsPage() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //startActivityForResult(intent, PERMISSION_SETTINGS_NAVIGATION)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (PermissionUtil().requestPermissionForCamera(this, false)) {
                openChoosePhoto()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (PermissionUtil().requestPermissionForCamera(this, false)) {
            openChoosePhoto()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.GONE

    }

    override fun onItemClicked(tag: String?, pos: String?) {
        val mediaList = viewModel.storyMedia.value
        mediaList?.removeAt(pos?.toInt()!!)
        viewModel.storyMedia.value = mediaList
        addStoryMediaAdapter.notifyDataSetChanged()
    }
}