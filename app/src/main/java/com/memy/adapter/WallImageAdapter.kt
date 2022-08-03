package com.memy.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.memy.R
import com.memy.listener.AdapterListener
import com.memy.pojo.WallData

class WallImageAdapter(
    var context: Context,
    var isAddStory: Boolean = false,
    var itemClickListener: AdapterListener,
    var data: List<WallData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_wall_images, parent, false)
        return VHItem(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = holder as WallImageAdapter.VHItem
        viewHolder.sLay.visibility = View.GONE
        if((data[position].media != null) && (data[position].media.size > 0) && (data[position].media[0].file.contains(".mp4"))){
            viewHolder.imgLay.visibility = View.GONE
            viewHolder.playerView.visibility = View.VISIBLE
            viewHolder.playLay.visibility = View.VISIBLE
            var player = ExoPlayer.Builder( /* context= */context)
                .build()
            val mediaItem = MediaItem.fromUri(data[position].media[0].file)
            player.addMediaItem(mediaItem)
            player.setPlayWhenReady(false)
            player.seekTo(1)
            holder.playerView.player = player
            player.addListener(object : Player.Listener   {
                override  fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {

                    }
                }
            })
            viewHolder.img_play.setTag(position)
            holder.img_play.setOnClickListener {
                val pos=viewHolder.img_play.getTag() as Int?
                itemClickListener.updateAction(1000,data[pos!!]);
            }
        }else {
            viewHolder.imgLay.visibility = View.VISIBLE
            viewHolder.playerView.visibility = View.GONE
            viewHolder.playLay.visibility = View.GONE
            var txtC = data[position].content
            /* if(!TextUtils.isEmpty(data[position].location)){
                txtC=txtC+"\n"+data[position].location
            }*/
            viewHolder.txtContent.visibility = View.GONE
            if (!TextUtils.isEmpty(txtC)) {
                viewHolder.txtContent.visibility = View.VISIBLE
                viewHolder.txtContent.text = txtC
            }
            if ((data[position].media != null) && (data[position].media.size > 0)){
                Glide.with(context)
                    .load(data[position].media[0].file)
                    .into(viewHolder.wall_image)
        }
            viewHolder.wall_image.setTag(position)
            viewHolder.wall_image.setOnClickListener {
                val pos=viewHolder.wall_image.getTag() as Int?
                itemClickListener.updateAction(1000,data[pos!!]);
            }
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var wall_image: AppCompatImageView
        var txtContent: AppCompatTextView
        var img_play: AppCompatImageView
        var playerView: StyledPlayerView
        var sLay: RelativeLayout
        var playLay: RelativeLayout
        var imgLay: RelativeLayout
        var addFloat: FloatingActionButton

        init {
            wall_image = itemView.findViewById<View>(R.id.wall_image) as AppCompatImageView
            img_play = itemView.findViewById<View>(R.id.img_play) as AppCompatImageView
            txtContent = itemView.findViewById<View>(R.id.txt_content) as AppCompatTextView
            playerView = itemView.findViewById<View>(R.id.videoPlayerView) as StyledPlayerView
            sLay = itemView.findViewById<View>(R.id.lay_add_story) as RelativeLayout
            imgLay = itemView.findViewById<View>(R.id.img_lay) as RelativeLayout
            playLay = itemView.findViewById<View>(R.id.playLay) as RelativeLayout
            addFloat = itemView.findViewById<View>(R.id.add_float) as FloatingActionButton
        }
    }

}