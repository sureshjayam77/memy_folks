package com.memy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.memy.databinding.ItemListAdapterBinding
import com.memy.listener.AdapterListener

class ItemListAdapter(val ctx : Context,val list:List<String>,val listener:AdapterListener) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListAdapter.ViewHolder {
        return ViewHolder(ItemListAdapterBinding.inflate(LayoutInflater.from(ctx),parent,false),listener)
    }

    override fun onBindViewHolder(holder: ItemListAdapter.ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
        return if(list != null) (list.size) else 0
    }

    class ViewHolder(val binding:ItemListAdapterBinding,val listener:AdapterListener):RecyclerView.ViewHolder(binding.root) {
        fun bind(str : String){
            binding.itemTextView.text = str
            binding.itemTextView.tag = str
            binding.itemTextView.setOnClickListener(View.OnClickListener {
                listener.updateAction(0,str)
            })
        }
    }
}