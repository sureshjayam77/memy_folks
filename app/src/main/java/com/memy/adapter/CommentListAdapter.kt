package com.memy.adapter

import android.content.Context
import android.view.ViewGroup
import com.memy.adapter.MyRecyclerAdapter
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.memy.R
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.memy.listener.AdapterListener
import com.memy.pojo.CommentObject
import com.memy.pojo.WallGroupData
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
class CommentListAdapter(var context:Context, var data:ArrayList<CommentObject>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_comment, parent, false)
        return VHItem(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder=holder as VHItem
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val simpleDateFormat1 = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val startDate=simpleDateFormat.parse(data[position].created_at)
        val formattedDate=simpleDateFormat1.format(startDate)
        itemHolder.txtDate.text=formattedDate
        itemHolder.txtContent.text=data[position].comment
        itemHolder.txtName.text=data[position].commenter.firstname
        Glide.with(context)
            .load(data[position].commenter.photo)   .placeholder(R.drawable.img_place_holder)
            .error(R.drawable.img_place_holder)
            .into(itemHolder.p_img)
       /* Glide
            .with(this)
            .load(data[position].)
            .centerCrop()
            .into(itemHolder.capture_img)*/
    }


    //increasing getItemcount to 1. This will be the row of header.
    override fun getItemCount(): Int {
        return data.size
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var p_img: CircleImageView
        var capture_img: AppCompatImageView
        var txtName: AppCompatTextView
        var txtDate: AppCompatTextView
        var txtContent: AppCompatTextView

        init {
            txtName = itemView.findViewById<View>(R.id.txt_name) as AppCompatTextView
            txtDate = itemView.findViewById<View>(R.id.txt_date) as AppCompatTextView
            txtContent = itemView.findViewById<View>(R.id.txt_content) as AppCompatTextView
            p_img = itemView.findViewById<View>(R.id.p_img) as CircleImageView
            capture_img = itemView.findViewById<View>(R.id.capture_img) as AppCompatImageView
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}