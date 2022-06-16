package com.memy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.memy.R
import com.memy.listener.CustomDropDownCallBack
import com.memy.pojo.SpinnerItem

class CustomDropDownAdapter (val context: Context, var dataSource: List<SpinnerItem>,var listener : CustomDropDownCallBack) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_sample_adapter, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.descriptionTextView.text = dataSource.get(position).name
        //vh.descriptionTextView.tag = dataSource.get(position)
        vh.descriptionTextView.setOnClickListener({
            if(listener != null){
                listener.dropDownItemClick(dataSource.get(position))
            }
        })

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val descriptionTextView: AppCompatTextView

        init {
            descriptionTextView = row?.findViewById(R.id.descriptionTextView) as AppCompatTextView
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent)
    }

}