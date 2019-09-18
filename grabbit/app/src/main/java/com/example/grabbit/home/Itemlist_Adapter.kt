package com.example.grabbit.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import kotlinx.android.synthetic.main.itemlist_view_onmain.view.*

class Itemlist_Adapter: RecyclerView.Adapter<CustomViewHolder>() {

    override fun getItemCount(): Int {
        return 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellforRow = layoutInflater.inflate(R.layout.itemlist_view_onmain, parent, false)

        return CustomViewHolder(cellforRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

    }
}

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v) {

}

