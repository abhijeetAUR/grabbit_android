package com.example.grabbit.bnhome.bnhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
        holder.itemNameHome.text = items[position].ITEMNAME.trim()
        holder.itemPriceHome.text = "Rs ${items[position].ITEMRATE}".trim()
        Glide.with(context).load(items[position].ITEMIMAGE).into(holder.itemImageHome)
    }

    inner class CustomViewHolder(itemView: View, listener: OnProductListClickListener?): RecyclerView.ViewHolder(itemView){

        val itemImageHome: ImageView = itemView.findViewById(R.id.img_home)
        val itemNameHome: TextView = itemView.findViewById(R.id.txt_name_home)
        val itemPriceHome: TextView = itemView.findViewById(R.id.txt_price_home)
        private val btnAddItemToCart: ImageButton = itemView.findViewById(R.id.btn_add_item_to_cart)
        init {
            btnAddItemToCart.setOnClickListener {
                if (listener != null){
                    listener!!.onProductListClick(adapterPosition)
                }
            }
        }

    }
}



