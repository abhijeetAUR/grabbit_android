package com.example.grabbit.bnhome.bnhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grabbit.R

class ItemsAdapter(private val items : ArrayList<HomeResponseList>,private val context: Context): RecyclerView.Adapter<ItemsAdapter.CustomViewHolder>() {
    interface OnProductListClickListener {
        fun onProductListClick(position: Int)
    }

    private var mOnProductListClick : OnProductListClickListener? = null

    fun setOnItemClickListener(onProductListClick: OnProductListClickListener){
        mOnProductListClick = onProductListClick
    }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.itemlist_view_onmain, parent, false)
        return CustomViewHolder(cellForRow, mOnProductListClick)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemNameHome.text = items[position].ITEMNAME
        holder.itemPriceHome.text = "Rs ${items[position].ITEMRATE}"
        Glide.with(context).load(items[position].ITEMIMAGE).into(holder.itemImageHome)
    }

    inner class CustomViewHolder(itemView: View, listener: OnProductListClickListener?): RecyclerView.ViewHolder(itemView){

        val itemImageHome: ImageView = itemView.findViewById(R.id.item_image_home)
        val itemNameHome: TextView = itemView.findViewById(R.id.item_name_home)
        val itemPriceHome: TextView = itemView.findViewById(R.id.item_price_home)
        private val btnAddItemToCart: Button = itemView.findViewById(R.id.add_item_to_cart)
        init {
            btnAddItemToCart.setOnClickListener {
                if (listener != null){
                    listener!!.onProductListClick(adapterPosition)
                }
            }
        }

    }
}



