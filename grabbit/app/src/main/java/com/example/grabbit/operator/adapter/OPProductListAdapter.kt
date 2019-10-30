package com.example.grabbit.operator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grabbit.R
import com.example.grabbit.operator.model.OPProductList

class OPProductListAdapter (private val items : ArrayList<OPProductList>, private val context: Context): RecyclerView.Adapter<OPProductListAdapter.CustomViewHolder>() {
    interface OnProductListClickListener {
        fun onProductListClick(position: Int)
    }

    private var mOnProductListClick : OnProductListClickListener? = null

    fun setOnItemClickListener(onProductListClick: OnProductListClickListener){
        mOnProductListClick = onProductListClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.itemlist_view_onmain, parent, false)
        return CustomViewHolder(cellForRow, mOnProductListClick)
    }

    override fun getItemCount(): Int = items.count()

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
            btnAddItemToCart.setImageResource(R.drawable.ic_chevron_right_black_24dp)
            btnAddItemToCart.setOnClickListener {
                if (listener != null){
                    listener!!.onProductListClick(adapterPosition)
                }
            }
        }

    }
}