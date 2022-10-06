package com.memy.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.memy.activity.GuideActivity
import com.memy.databinding.HelpAdapterLayoutBinding
import com.memy.pojo.HelpItemObj
import com.memy.utils.Constents

class HelpAdapter(val ctx : Context,val list : List<HelpItemObj>) : RecyclerView.Adapter<HelpAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpAdapter.ViewHolder {
        return (ViewHolder(ctx,HelpAdapterLayoutBinding.inflate(LayoutInflater.from(ctx),parent,false)))
    }

    override fun onBindViewHolder(holder: HelpAdapter.ViewHolder, position: Int) {
        holder.bind(list.get(position),position)
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else 0
    }

    class ViewHolder(val ctx : Context,val binding : HelpAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : HelpItemObj,pos : Int){
            binding.labelTextView.text = item.label ?: ""
            binding.parentLayout.tag = pos
            binding.imageView.setImageResource(item.icon)
            binding.parentLayout.setOnClickListener { v ->
                val tagValue : Int = (v.getTag()) as Int
                val intent = Intent(ctx, GuideActivity::class.java)
                intent.putExtra(Constents.INTENT_BUNDLE_GUIDE_ARGUMENT_POS_TAG,tagValue)
                ctx.startActivity(intent)
             }
        }
    }
}