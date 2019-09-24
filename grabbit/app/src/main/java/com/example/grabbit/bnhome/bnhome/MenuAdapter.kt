package com.example.grabbit.bnhome.bnhome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R

class MenuAdapter(private val categories : ArrayList<String>): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var mOnProductCategoryListener: OnProductCategoryListener? = null
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
        holder.categoryName.text = categories[position]
    }
    inner class ViewHolder(itemView: View, onProductCategoryListener: OnProductCategoryListener?) : RecyclerView.ViewHolder(itemView){
        val categoryName : TextView = itemView.findViewById(R.id.category_name)
        init {
            itemView.setOnClickListener {
                if (onProductCategoryListener != null){
                    onProductCategoryListener!!.onProductCategoryClick(adapterPosition)
                }
            }
        }


    }
}