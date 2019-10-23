package com.example.grabbit.bnhome.bnhome.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import com.example.grabbit.utils.SingletonProductDataHolder

class MenuAdapter(private val categories : ArrayList<String>): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var mOnProductCategoryListener: OnProductCategoryListener? = null
    private val singletonProductDataHolder = SingletonProductDataHolder.instance

    interface OnProductCategoryListener{
        fun onProductCategoryClick(position: Int)
    }

    fun setOnItemClickListener(productCategoryListener: OnProductCategoryListener){
        mOnProductCategoryListener = productCategoryListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.category_view, parent, false)
        return ViewHolder(view, mOnProductCategoryListener)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryName.text = categories[position].toLowerCase().capitalize()
        if (singletonProductDataHolder.lstBtnNameAndStatus[position].status){
            holder.selectedCategoryArrow.visibility = View.VISIBLE
            holder.categoryName.setTextColor(Color.parseColor("#252729"))
        } else{
            holder.selectedCategoryArrow.visibility = View.GONE
            holder.categoryName.setTextColor(Color.parseColor("#9d968d"))
        }
    }
    inner class ViewHolder(itemView: View, onProductCategoryListener: OnProductCategoryListener?) : RecyclerView.ViewHolder(itemView){
        val categoryName : TextView = itemView.findViewById(R.id.category_name)
        val selectedCategoryArrow: ImageView = itemView.findViewById(R.id.img_selected_category)
        init {
            itemView.setOnClickListener {
                if (onProductCategoryListener != null){
                    onProductCategoryListener!!.onProductCategoryClick(adapterPosition)
                }
            }
        }


    }
}