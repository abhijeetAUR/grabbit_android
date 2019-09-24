package com.example.grabbit.bnhome.bncart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R

class CartItemList_Adapter: RecyclerView.Adapter<CustomViewHolderCart>() {

    override fun getItemCount(): Int {
        return 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderCart {
        val layoutInflatercart = LayoutInflater.from(parent.context)
        val cellforRowcart = layoutInflatercart.inflate(R.layout.addeditems_cartlistview, parent, false)

        return CustomViewHolderCart(cellforRowcart)
    }

    override fun onBindViewHolder(holder: CustomViewHolderCart, position: Int) {

    }

}
class CustomViewHolderCart(v: View): RecyclerView.ViewHolder(v) {

}


