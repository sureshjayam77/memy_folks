package com.memy.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
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
import com.memy.pojo.NotificationData
import com.memy.utils.Constents
import de.hdodenhof.circleimageview.CircleImageView


class NotificationListAdapter(val ctx : Context, val list : List<NotificationData>): RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    private lateinit var listener : ItemClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text=list[position].name
        holder.descText.text=list[position].activity
        holder.timeText.text=list[position].humandate
       if(!TextUtils.isEmpty(list[position].photo)){
           Glide
               .with(ctx)
               .load(list[position].photo)
               .centerCrop()
               .placeholder(R.drawable.img_place_holder)
               .error(R.drawable.img_place_holder)
               .into(holder.profileImage)
       }
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else(0)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val titleText: AppCompatTextView = itemView.findViewById(R.id.txt_title)
        val descText: AppCompatTextView = itemView.findViewById(R.id.txt_des)
        val timeText: AppCompatTextView = itemView.findViewById(R.id.txt_time)
        val profileImage: CircleImageView = itemView.findViewById(R.id.img_profile)
    }
}