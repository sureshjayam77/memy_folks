package com.memy.adapter

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
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
import com.memy.pojo.WallGroupData
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyRecyclerAdapter(var context:Context, var itemClickListener: AdapterListener, var data:ArrayList<WallData>, var mid:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     val TYPE_HEADER : Int = 0
    private val TYPE_LIST : Int = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == TYPE_HEADER)
        {
            val header = LayoutInflater.from(parent.context).inflate(R.layout.wall_header_adapter,parent,false)
            return VHItemHeader(header)
        }

        val header = LayoutInflater.from(parent.context).inflate(R.layout.adapter_wall_images,parent,false)
        return VHItem(header)
    }

    override fun getItemViewType(position: Int): Int {
        if(TextUtils.isEmpty(data[position].mid_id))
        {
            return TYPE_HEADER
        }
        return TYPE_LIST
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
          if(holder is VHItemHeader){
              Log.d("TAG", "onBindViewHolder: "+"header")
            val itemHolder= holder
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val simpleDateFormat1 = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            if(!TextUtils.isEmpty(data[position].id)) {
                val startDate = simpleDateFormat.parse(data[position].id)
                val formattedDate = simpleDateFormat1.format(startDate)
                itemHolder.txtTitle.text = formattedDate
            }
        }else if(holder is VHItem){
              Log.d("TAG", "onBindViewHolder: "+"body")
            var viewHolder = holder
            viewHolder.sLay.visibility = View.GONE

            viewHolder.parentLay.visibility=View.VISIBLE
            if((data[position].media != null) && (data[position].media!!.size > 0) && (data[position].media!![0].file.contains(".mp4"))){
                viewHolder.imgLay.visibility = View.GONE
                viewHolder.playerView.visibility = View.VISIBLE
                viewHolder.playLay.visibility = View.VISIBLE
                var player = ExoPlayer.Builder( /* context= */context)
                    .build()
                val mediaItem = MediaItem.fromUri(data[position].media!![0].file)
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

                if ((data[position].attachments != null) && (data[position].attachments!!.size > 0)){
                    val imgUrl=data[position].attachments!![0].file
                    Log.d("TAG", "onBindViewHolder: "+imgUrl)
                    Glide.with(context)
                        .load(imgUrl)
                        .into(viewHolder.wall_image)
                }else{
                    if ((data[position].media != null) && (data[position].media!!.size > 0)){
                        Glide.with(context)
                            .load(data[position].media!![0].file)
                            .into(viewHolder.wall_image)
                    }
                }
                viewHolder.wall_image.setTag(position)
                viewHolder.wall_image.setOnClickListener {
                    val pos=viewHolder.wall_image.getTag() as Int?
                    if(data[pos!!].media!!.size>0)
                    itemClickListener.updateAction(1000,data[pos!!]);
                }
            }
            if(mid.equals(data[position!!].mid_id)){
                holder.dlt_img.visibility=View.GONE
            }else{
                holder.dlt_img.visibility=View.GONE
            }
            holder.dlt_img.setTag(position)
            holder.dlt_img.setOnClickListener {
                val pos=viewHolder.wall_image.getTag() as Int?
                itemClickListener.updateAction(1001,data[pos!!]);
            }

            /*  val itemHolder= holder
              itemHolder.recyWall.layoutManager=GridLayoutManager(context,3)
              *//* var list:ArrayList<WallData>?= ArrayList()
             list!!.addAll(data[position].data)
            *//*
            itemHolder.recyWall.adapter=WallImageAdapter(context,position==1,itemClickListener,list.toList(),mid)
*/
        }


    }

    /*//    need to override this method
    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0||position == 2
    }*/

    //increasing getItemcount to 1. This will be the row of header.
    override fun getItemCount(): Int {
        return data.size
    }

    /*internal inner class VHHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: AppCompatTextView
        init {
            txtTitle = itemView.findViewById<View>(R.id.txt_header) as AppCompatTextView
        }
    }*/

  /*  internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyWall: RecyclerView

        init {
            recyWall = itemView.findViewById<View>(R.id.recy_wall) as RecyclerView
        }
    }*/
    internal inner class VHItemHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: AppCompatTextView
        init {
            txtTitle = itemView.findViewById<View>(R.id.txt_header) as AppCompatTextView
        }
    }
    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var wall_image: AppCompatImageView
        var dlt_img: AppCompatImageView
        var txtContent: AppCompatTextView
        var img_play: AppCompatImageView
        var playerView: StyledPlayerView
        var sLay: RelativeLayout
        var playLay: RelativeLayout
        var imgLay: RelativeLayout
        var addFloat: FloatingActionButton
        var parentLay: LinearLayout

        init {
            wall_image = itemView.findViewById<View>(R.id.wall_image) as AppCompatImageView
            parentLay = itemView.findViewById<View>(R.id.parentLay) as LinearLayout
            dlt_img = itemView.findViewById<View>(R.id.img_delete) as AppCompatImageView
            img_play = itemView.findViewById<View>(R.id.img_play) as AppCompatImageView
            txtContent = itemView.findViewById<View>(R.id.txt_content) as AppCompatTextView
            playerView = itemView.findViewById<View>(R.id.videoPlayerView) as StyledPlayerView
            sLay = itemView.findViewById<View>(R.id.lay_add_story) as RelativeLayout
            imgLay = itemView.findViewById<View>(R.id.img_lay) as RelativeLayout
            playLay = itemView.findViewById<View>(R.id.playLay) as RelativeLayout
            addFloat = itemView.findViewById<View>(R.id.add_float) as FloatingActionButton
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}