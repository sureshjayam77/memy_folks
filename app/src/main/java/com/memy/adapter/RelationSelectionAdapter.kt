package com.memy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.memy.R
import com.memy.databinding.RelationItemLayoutBinding
import com.memy.listener.AdapterListener
import com.memy.pojo.RelationSelectionObj

class RelationSelectionAdapter(ctx : Context,val list : List<RelationSelectionObj>,val listener : AdapterListener) : RecyclerView.Adapter<RelationSelectionAdapter.ViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(RelationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val obj : RelationSelectionObj = list.get(position)
        holder.bind(obj,position)
        holder.binding.parentLayout.tag = obj
        holder.binding.parentLayout.setOnClickListener(View.OnClickListener { v ->
            val obj1 : RelationSelectionObj = v.getTag() as RelationSelectionObj
            if((obj1.is_applicable == null) || (obj1.is_applicable == true)) {
                listener.updateAction(obj1.id as Int, obj1)
            }
        })
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else 0
    }

    class ViewModel(val binding : RelationItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(obj : RelationSelectionObj,pos : Int){
            when(obj.id){
                1 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_father_popup)
                }
                2 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_mother_popup)
                }
                3 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_wife_popup)
                }
                4 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_son_popup)
                }
                5 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_daughter_popup)
                }
                6 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_pet_popup)
                }
                7 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_brother_popup)//
                }
                8 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_sister_popup)//
                }
                9 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_brother_popup)
                }
                10 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_add_sister_popup)
                }
                1001 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_edit_profile_popup)
                }
                1002 -> {
                    binding.iconImageView.setImageResource(R.drawable.ic_remove_popup)
                }
            }
            val isAdded = obj.is_applicable
            binding.iconImageView.alpha = if(isAdded==false) (0.5f) else(1f)
            binding.relationTextView.alpha = if(isAdded==false) (0.5f) else(1f)
            binding.relationTextView.text = obj.name
        }
    }
}