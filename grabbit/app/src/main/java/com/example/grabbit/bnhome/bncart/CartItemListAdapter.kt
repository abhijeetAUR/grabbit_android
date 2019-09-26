package com.example.grabbit.bnhome.bncart

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
import com.example.grabbit.bnhome.bnhome.HomeResponseList
import com.example.grabbit.bnhome.bnhome.SingletonProductDataHolder

class CartItemListAdapter(private val lstHomeResponseList : ArrayList<HomeResponseList>, private val context: Context): RecyclerView.Adapter<CartItemListAdapter.CustomViewHolderCart>() {
    private val singletonProductDataHolder = SingletonProductDataHolder.instance
    internal lateinit var txtTotalItems: TextView
    interface OnBtnRemoveClickListener{
        fun onBtnRemoveClick(position: Int)
    }

    private var mOnBtnRemoveClickListener: OnBtnRemoveClickListener? = null

    fun setOnClickListener(listener: OnBtnRemoveClickListener){
        mOnBtnRemoveClickListener = listener
    }

    override fun getItemCount() = lstHomeResponseList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderCart {
        val cellForRowCart = LayoutInflater.from(parent.context).inflate(R.layout.addeditems_cartlistview, parent, false)
        return CustomViewHolderCart(cellForRowCart, mOnBtnRemoveClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolderCart, position: Int) {
        holder.txtProductName.text = lstHomeResponseList[position].ITEMNAME.trim()
        holder.txtProductPrice.text = "Rs ${lstHomeResponseList[position].ITEMRATE}".trim()
        if (singletonProductDataHolder.lstProductsAddedToCart.count() > 0 ){
//            holder.txtTotalItems.text = "Total Items: ${singletonProductDataHolder.lstProductsAddedToCart.count()}"
        }
        Glide.with(context).load(lstHomeResponseList[position].ITEMIMAGE).into(holder.imgProduct)
    }

    class CustomViewHolderCart(itemView: View, listener: OnBtnRemoveClickListener?): RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.img_product)
        val txtProductName : TextView = itemView.findViewById(R.id.txt_product_name)
        val txtProductPrice : TextView = itemView.findViewById(R.id.txt_product_price)
//        val txtTotalItems = itemView.findViewById<TextView>(R.id.txt_total_items)
        private val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
        init {
            if (listener != null){
                btnRemove.setOnClickListener {
                    listener!!.onBtnRemoveClick(adapterPosition)
                }
            }
        }
    }
}



