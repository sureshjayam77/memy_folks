package com.memy.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.memy.R
import com.memy.adapter.CommentListAdapter
import com.memy.adapter.MyRecyclerAdapter
import com.memy.databinding.ActivityCommentViewBinding
import com.memy.databinding.ActivityPostViewBinding
import com.memy.databinding.StoryMediaBottomSheetBinding
import com.memy.pojo.*
import com.memy.viewModel.AddEventViewModel
import com.teresaholfeld.stories.StoriesProgressView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CommentViewActivity : AppBaseActivity(){
    lateinit var binding: ActivityCommentViewBinding
    lateinit var viewModel: AddEventViewModel
    var player: ExoPlayer? = null
    var file: String? = null
    var uploadedFile:File?=null
    var wallData: WallData? = null
    var commentList: ArrayList<CommentObject>? = ArrayList()
    private val SELECT_PICTURE = 9001
    private val SELECT_VIDEO = 9002
    companion object {
        private const val PROGRESS_COUNT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_view)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)
        wallData = intent.getSerializableExtra("file") as WallData?
        viewModel.commentRes.observe(this, this::getWallDataResponse)
        viewModel.addFamilyRes.observe(this, this::validateAddFamilyRes)
        binding.backIconImageView.setOnClickListener {
            onBackPressed()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvComments.layoutManager = linearLayoutManager
        binding.btnPost.setOnClickListener {
            postComment()
        }
        binding.attachmentImg.setOnClickListener {
            openChoosePhoto()
        }
        binding.clearImg.setOnClickListener {
            uploadedFile=null
            binding.captureLay.visibility=View.GONE
        }
        if(TextUtils.isEmpty(wallData!!.location)){
            viewModel.getCommentList(wallData!!.id)
        }else{
            viewModel.getEventCommentList(wallData!!.id)
        }

    }
    private fun validateAddFamilyRes(res: CommonResponse) {
        if (res.statusCode == 200) {
            showToast("Comment added successfully")
            uploadedFile=null
            binding.captureLay.visibility=View.GONE
            if(TextUtils.isEmpty(wallData!!.location)){
                viewModel.getCommentList(wallData!!.id)
            }else{
                viewModel.getEventCommentList(wallData!!.id)
            }
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }
    private fun getWallDataResponse(res: CommentResult) {
        if (res.statusCode == 200) {
           if(res.data!=null&&res.data!!.isNotEmpty()){
               commentList= ArrayList()
               commentList!!.addAll(res.data!!)
               commentList?.reverse()
               binding.rvComments?.adapter = CommentListAdapter(this, commentList!!)
               Log.d("", "")
           }
        }
    }

    private fun postComment() {
        if(TextUtils.isEmpty(wallData!!.location)){
            if (TextUtils.isEmpty(binding.edtComment.text.toString())) return
            viewModel.addComment(
                prefhelper.fetchUserData()?.mid.toString(),
                uploadedFile,
                binding.edtComment.text.toString(),
                wallData!!.id
            )
            binding.edtComment.setText("")
        }else{
            if (TextUtils.isEmpty(binding.edtComment.text.toString())&&uploadedFile==null)return
            viewModel.addEventComment(
                prefhelper.fetchUserData()?.mid.toString(),
                uploadedFile,
                binding.edtComment.text.toString(),
                wallData!!.id
            )
            binding.edtComment.setText("")
        }

    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {
    }

    private fun openChoosePhoto() {
        val photoBottomSheet = BottomSheetDialog(this, R.style.bottomSheetDialogTheme)
        val storyMediaBottomSheetBinding: StoryMediaBottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.story_media_bottom_sheet, null, false
        )
        storyMediaBottomSheetBinding.videoTextView.visibility=View.GONE
        storyMediaBottomSheetBinding.photoTextView.setOnClickListener({photoBottomSheet.dismiss()
        photoPicker()})
       /* storyMediaBottomSheetBinding.videoTextView.setOnClickListener({photoBottomSheet.dismiss()
        videoPicker()})*/
        storyMediaBottomSheetBinding.audioTextView.visibility=View.GONE
        storyMediaBottomSheetBinding.cancelTextView.setOnClickListener({ photoBottomSheet.dismiss() })
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
    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {

            uploadedFile=getFile(this, intent?.data!!)
        } else if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {
            if (intent != null) {
                uploadedFile=getFile(this, intent.data!!)
            }
        }
        if (uploadedFile != null) {
            binding.captureLay.visibility=View.VISIBLE
            Glide
                .with(this)
                .load(uploadedFile)
                .centerCrop()
                .into(binding.captureImg)
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                ins?.let {
                    createFileFromStream(
                        it,
                        destinationFilename
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}