package com.memy.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.memy.R
import com.memy.databinding.ActivityWallBinding
import com.memy.pojo.CommonResponse
import com.memy.utils.Utils
import com.memy.viewModel.AddEventViewModel
import java.io.File

class WallPostActivity : AppBaseActivity() {
    lateinit var binding: ActivityWallBinding
    lateinit var viewModel: AddEventViewModel
    var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wall)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)
        viewModel.addFamilyRes.observe(this, this::validateAddFamilyRes)
        file = intent.getSerializableExtra("file") as File?
        if (Utils.isVideoFile(file!!.path)) {
            playVideo()
        } else {
            binding.imgWall.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(file)
                .centerCrop()
                .into(binding.imgWall)
        }

        binding.btnPost.setOnClickListener {
            showProgressBar()
            viewModel.addStatusEvent(prefhelper.fetchUserData()?.mid.toString(), file!!, binding.edtComment.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    private fun playVideo() {
        binding.videoPlayerView.visibility = View.VISIBLE
        var player = binding.videoPlayerView.getPlayer()
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(file))
            player.addMediaItem(mediaItem)
            player.playWhenReady = true
            binding.videoPlayerView.setPlayer(player)

        }
    }

    private fun validateAddFamilyRes(res: CommonResponse) {
        hideProgressBar()
        if (res.statusCode == 200) {
            showToast("Post added successfully")
            val intent= Intent()
            intent.putExtra("is_done",true)
            setResult(Activity.RESULT_OK,intent)
            finish()
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun dialogPositiveCallBack(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun dialogNegativeCallBack() {
        TODO("Not yet implemented")
    }

    private fun showProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressInclude.progressBarLayout.visibility = View.GONE

    }
}