package com.memy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.memy.R
import com.memy.databinding.RelationItemLayoutBinding
import com.memy.pojo.RelationSelectionObj

class RelationSelectionAdapter(ctx : Context,val list : List<RelationSelectionObj>) : RecyclerView.Adapter<RelationSelectionAdapter.ViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        return ViewModel(RelationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bind(list.get(position),position)
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else 0
    }

    class ViewModel(val binding : RelationItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(obj : RelationSelectionObj,pos : Int){
            when(obj.id){
                1 -> {
                    val isAdded = obj.is_applicable
                    binding.iconImageView.setImageResource(R.drawable.ic_add_father_popup)
                    binding.iconImageView.alpha = if(isAdded==false) (0.5f) else(1f)
                    binding.relationTextView.alpha = if(isAdded==false) (0.5f) else(1f)
                }
                2 -> {
                    val isAdded = obj.is_applicable
                    binding.iconImageView.setImageResource(R.drawable.ic_add_mother_popup)
                    binding.iconImageView.alpha = if(isAdded==false) (0.5f) else(1f)
                    binding.relationTextView.alpha = if(isAdded==false) (0.5f) else(1f)
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
            }
        }
    }
}