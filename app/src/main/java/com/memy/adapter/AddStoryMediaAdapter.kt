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
import com.google.android.exoplayer2.SimpleExoPlayer
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
                    var player = ExoPlayer.Builder( /* context= */ctx)
                        .build()
                    val mediaItem = MediaItem.fromUri(itemObj.fileURI)
                    player.addMediaItem(mediaItem)
                    player.setPlayWhenReady(true)
                    holder.playerView.player = player
                }
                holder.closeImageView.tag = position.toString()
                holder.closeImageView.setOnClickListener(View.OnClickListener { it ->
                    if(listener != null){
                        listener.onItemClicked(Constents.ONCLICK_DELETE_MEDIA,it.getTag().toString())
                    }
                })
            }

        }
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