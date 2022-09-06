package com.memy.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.memy.R
import com.memy.databinding.AvatarImageLayoutBinding
import com.memy.listener.AdapterListener
import com.memy.pojo.AvatarImageListRes
import com.memy.pojo.DataItem

class AvatarImageAdapter(val ctx : Context, val list : List<DataItem>?, val listener: AdapterListener) : RecyclerView.Adapter<AvatarImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvatarImageAdapter.ViewHolder {
        val binding = AvatarImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarImageAdapter.ViewHolder, position: Int) {
        holder.bind(list?.get(position),ctx,listener)
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else(0)
    }
    class ViewHolder(val binding: AvatarImageLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(obj : DataItem?,ctx:Context,listener: AdapterListener){
            if ((obj != null) && (!TextUtils.isEmpty(obj.avatar))) {
                Glide.with(ctx)
                    .load(obj.avatar)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile_male)
                    .error(R.drawable.ic_profile_male)
                    .into(binding.avatarImageView)
                binding.avatarImageView.setOnClickListener(View.OnClickListener {
                    listener.updateAction(-1,obj)
                })
            }
        }
    }
}