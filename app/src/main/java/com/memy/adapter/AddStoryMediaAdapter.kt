package com.memy.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.memy.R
import com.memy.pojo.AddStoryMediaObj
import com.memy.utils.StoryMediaType
import com.google.android.exoplayer2.util.EventLogger

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.memy.utils.Constents


class AddStoryMediaAdapter(val ctx : Context, val list : List<AddStoryMediaObj>): RecyclerView.Adapter<AddStoryMediaAdapter.ViewHolder>() {

    private lateinit var listener : ItemClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_story_media_adapter, parent, false)
        listener = ctx as ItemClickListener
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.visibility = View.GONE
        holder.playerView.visibility = View.GONE
        if((list != null) && (list.size > 0)) {
            val itemObj: AddStoryMediaObj = list.get(position)
            if (itemObj != null) {
                if(itemObj.mediaType == StoryMediaType.IMAGE){
                    holder.imageView.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(itemObj.filePath)) {
                        Glide
                            .with(ctx)
                            .load(itemObj.filePath)
                            .centerCrop()
                            .placeholder(R.drawable.img_place_holder)
                            .error(R.drawable.img_place_holder)
                            .into(holder.imageView)
                    }
                }else{
                    holder.playerView.visibility = View.VISIBLE
                    if((holder.playerView.player != null) && (holder.playerView.player?.isPlaying == true)){
                        holder.playerView.player?.release()
                    }

                    if((holder.playerView.player != null) && (holder.playerView.player?.mediaItemCount!! > 0)) {
                        holder.playerView.player?.removeMediaItem(0)
                    }
                    var player = ExoPlayer.Builder( /* context= */ctx)
                        .build()
                    val mediaItem = MediaItem.fromUri(itemObj.fileURI)
                    player.addMediaItem(mediaItem)
                    player.setPlayWhenReady(true)
                    player.seekTo(1)
                    holder.playerView.player = player
                    player.addListener(object : Player.Listener   {
                        override  fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_READY) {

                            }
                        }
                    })
                }
                holder.closeImageView.tag = position.toString()
                holder.closeImageView.setOnClickListener(View.OnClickListener { it ->
                    if((holder.playerView.player != null) && (holder.playerView.player?.isPlaying == true)){
                        holder.playerView.player?.release()
                    }
                    if(listener != null){
                        listener.onItemClicked(Constents.ONCLICK_DELETE_MEDIA,it.getTag().toString())
                    }
                })
            }

        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if((holder.playerView.player != null) && (holder.playerView.player?.isPlaying == true)){
            holder.playerView.player?.release()
        }
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else(0)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val imageView: AppCompatImageView = itemView.findViewById(R.id.storyImageView)
        val playerView: StyledPlayerView = itemView.findViewById(R.id.videoPlayerView)
        val closeImageView: AppCompatImageView = itemView.findViewById(R.id.closeImageView)
    }
}