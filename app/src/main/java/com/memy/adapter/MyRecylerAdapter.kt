package com.memy.adapter

import android.content.Context
import android.view.ViewGroup
import com.memy.adapter.MyRecyclerAdapter
import android.view.LayoutInflater
import android.view.View
import com.memy.R
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.memy.listener.AdapterListener
import com.memy.pojo.WallGroupData
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyRecyclerAdapter(var context:Context, var itemClickListener: AdapterListener, var data:ArrayList<WallGroupData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_wall_item, parent, false)
        return VHItem(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder=holder as VHItem
        itemHolder.recyWall.layoutManager=GridLayoutManager(context,3)
        itemHolder.recyWall.adapter=WallImageAdapter(context,position==1,itemClickListener,data[position].data)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val simpleDateFormat1 = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val startDate=simpleDateFormat.parse(data[position].startedDate)
        val formattedDate=simpleDateFormat1.format(startDate)
        itemHolder.txtTitle.text=formattedDate
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

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyWall: RecyclerView
        var txtTitle: AppCompatTextView

        init {
            txtTitle = itemView.findViewById<View>(R.id.txt_header) as AppCompatTextView
            recyWall = itemView.findViewById<View>(R.id.recy_wall) as RecyclerView
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}