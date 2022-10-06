package com.memy.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.memy.R
import com.memy.databinding.ActivityPostViewBinding
import com.memy.pojo.CommonResponse
import com.memy.pojo.WallData
import com.memy.viewModel.AddEventViewModel
import com.teresaholfeld.stories.StoriesProgressView
import java.text.SimpleDateFormat
import java.util.*
class PostViewActivity : AppBaseActivity(), StoriesProgressView.StoriesListener {
    lateinit var binding: ActivityPostViewBinding
    lateinit var viewModel: AddEventViewModel
    var player: ExoPlayer? = null
    var file: String? = null
    var wallData: WallData? = null
    private var storiesProgressView: StoriesProgressView? = null
    private var counter = 0
    private var isOpenKeyBoard = false

    companion object {
        private const val PROGRESS_COUNT = 1
    }
    private var pressTime = 0L
    private var limit = 500L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_view)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)
        viewModel.deleteWallRes.observe(this, this::deleteWallRes)

        wallData = intent.getSerializableExtra("file") as WallData?
        file = wallData?.media?.get(0)?.file
        storiesProgressView = binding.stories
        storiesProgressView?.setStoriesListener(this) // <- set listener
        storiesProgressView?.setStoriesCount(PROGRESS_COUNT) // <- set stories
        binding.backIconImageView.setOnClickListener { onBackPressed()
        }
        if (file!!.contains(".mp4")) {
            playVideo()
            binding.wallImageLay.visibility = View.GONE
            binding.videoPlayerView.visibility = View.VISIBLE
        } else {
            Glide
                .with(this)
                .load(file)
                .centerCrop()
                .into(binding.wallImage)
            storiesProgressView?.setStoryDuration(3000L) // <- set a story duration
            storiesProgressView?.setStoriesListener(this) // <- set listener
            counter = 2
            storiesProgressView?.startStories() // <- start progress
            if (!TextUtils.isEmpty(wallData?.location)) {
                binding.eventLay.visibility = View.VISIBLE
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val simpleDateFormat1 =
                    SimpleDateFormat("MMM dd,yyyy\n hh:mm a", Locale.ENGLISH)
                val startDate = simpleDateFormat.parse(wallData?.event_start_date)
                val formattedDate = simpleDateFormat1.format(startDate)
                binding.wallImageLay.visibility = View.VISIBLE
                binding.addressTextView.text = wallData?.location
                binding.contentTextView.text = wallData?.content
                binding.dateTextView.text = formattedDate
                binding.dateTextView.text = formattedDate
                if(wallData?.host1_details?.size?:0>0){
                    binding.host1TextView.text = "Host: "+ wallData?.host1_details?.get(0)?.firstname
                    if(wallData?.host2_details?.size?:0> 0){
                        binding.host1TextView.text =  binding.host1TextView.text.toString()+" & "+wallData?.host2_details?.get(0)?.firstname
                    }
                }
                if(!TextUtils.isEmpty(wallData?.media_link)){
                    binding.host3TextView.text ="Click here to view link"
                    binding.host3TextView.setOnClickListener {
                        try{
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(wallData?.media_link)))
                        }catch (ex:Exception){
                            ex.message
                        }
                    }
                }
                if (wallData?.location_pin.equals("0")) {
                    binding.contentTextView.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.txtInfo.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.dateTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.host1TextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.host2TextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.host3TextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.addressTextView.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                } else {
                    binding.contentTextView.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    binding.txtInfo.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.dateTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.host1TextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.host2TextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.host3TextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.addressTextView.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                }

            }
        }
        Glide.with(this)
            .load(wallData!!.photo)   .placeholder(R.drawable.img_place_holder)
            .error(R.drawable.img_place_holder)
            .into(binding.pImg)
        binding.txtName.text=wallData!!.firstname
        binding.btnPost.setOnClickListener {
            val intent=Intent(this,CommentViewActivity::class.java)
            intent.putExtra("file",wallData)
           startActivity(intent)
        }
        binding.parentLay.setOnTouchListener({ v, event ->
            if (event.getAction() == MotionEvent.ACTION_UP) {
                val now = System.currentTimeMillis()
                player?.play()
                storiesProgressView?.resume()
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pressTime = System.currentTimeMillis()
                player?.pause()
                storiesProgressView?.pause()
            }
            true
        })
        binding.btnUpload.setOnClickListener {
            val intent=Intent(this,CommentViewActivity::class.java)
            intent.putExtra("file",wallData)
            startActivity(intent)
        }
        binding.btnWish.setOnClickListener {
            val intent=Intent(this,CommentViewActivity::class.java)
            intent.putExtra("file",wallData)
            startActivity(intent)
        }
        binding.parentLay.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff = binding.parentLay.rootView.height - binding.parentLay.height
                if (heightDiff > dpToPx(
                        this@PostViewActivity,
                        200f
                    )
                ) {
                    isOpenKeyBoard = true
                    player?.pause()
                    storiesProgressView?.pause()
                } else {
                    if (isOpenKeyBoard) {
                        isOpenKeyBoard = false
                        player?.play()
                        storiesProgressView?.resume()
                    }
                }
            }
        })
        if(prefhelper.fetchUserData()?.mid.toString().equals(wallData!!.mid_id)){
            binding.btnDelete.visibility=View.VISIBLE
        }else{
            binding.btnDelete.visibility=View.GONE
        }
        binding.btnDelete.setOnClickListener {
            var isWall=true
            if(!TextUtils.isEmpty(wallData!!.location_pin)){
                isWall=false
            }
            deleteDialog(isWall,wallData!!.id)
        }
        binding.txtContent.text = wallData?.content
    }
    private fun deleteWallRes(res: CommonResponse) {
        if (res.statusCode == 200) {
            showToast("Deleted successfully")
            finish()
        } else {
            res.errorDetails?.message?.let { showToast(it) }
        }
    }
    fun deleteDialog(isWall:Boolean,id:String){
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Delete "+if(isWall){"Wall"}else "Event")
            .setMessage("Are you sure you want to delete this wall?")
            .setPositiveButton("Yes",
                { dialog, which ->  dialog.dismiss()
                    if(isWall){viewModel.deleteWall(id,prefhelper.fetchUserData()?.mid.toString())}else{
                        viewModel.deleteEvent(id,prefhelper.fetchUserData()?.mid.toString())
                    } })
            .setNegativeButton("No", null)
            .show()
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    private fun playVideo() {
        binding.videoPlayerView.visibility = View.VISIBLE
        player = ExoPlayer.Builder( /* context= */this)
            .build()
        val mediaItem = MediaItem.fromUri(file!!)
        player?.addMediaItem(mediaItem)
        player?.setPlayWhenReady(true)
        player?.seekTo(1)
        binding.videoPlayerView.player = player
        player?.prepare()
        Log.d("duration", player?.duration.toString())
        player?.duration?.let { storiesProgressView?.setStoryDuration(it) } // <- set a story duration
        binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_play).visibility = View.GONE
        binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_pause).visibility = View.VISIBLE
        binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_play).setOnClickListener {
            binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_play).visibility = View.GONE
            binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_pause).visibility =
                View.VISIBLE
            player?.play()
        }
        binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_pause).setOnClickListener {
            binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_pause).visibility = View.GONE
            binding.videoPlayerView.findViewById<ImageButton>(R.id.exo_play).visibility =
                View.VISIBLE
            player?.pause()
        }
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    val realDurationMillis: Long = player?.duration!!
                    storiesProgressView?.setStoryDuration(realDurationMillis)
                    storiesProgressView?.startStories() // <- start progress

                }
            }
        })
    }

    private fun postComment() {
        if (TextUtils.isEmpty(binding.edtComment.text.toString())) return
        viewModel.addComment(
            prefhelper.fetchUserData()?.mid.toString(),
            null,
            binding.edtComment.text.toString(),
            wallData!!.id
        )
        binding.edtComment.setText("")
        showToast("Comment added successfully")
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun dialogPositiveCallBack(id: Int?) {

    }

    override fun dialogNegativeCallBack() {
    }

    override fun onComplete() {
        //finish()
    }

    override fun onNext() {
        TODO("Not yet implemented")
    }

    override fun onPrev() {
        TODO("Not yet implemented")
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
        // Very important !
        storiesProgressView?.destroy()
        player?.stop()
        super.onDestroy()
    }


}