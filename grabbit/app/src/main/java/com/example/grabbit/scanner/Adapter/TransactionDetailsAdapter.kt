package com.example.grabbit.scanner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import com.example.grabbit.scanner.Model.TransactionDetailsResponse

class TransactionDetailsAdapter(private val items : ArrayList<TransactionDetailsResponse>, private val context: Context): RecyclerView.Adapter<TransactionDetailsAdapter.CustomViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.transaction_details_view, parent, false))
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemNameHome.text = items[position].itemName.trim()
        holder.itemPriceHome.text = "Rs ${items[position].AMOUNT}".trim()
        holder.itemPaymentMode.text = items[position].PAYMENTMODE
        holder.itemQuantity.text = items[position].QUANTITY.toString()

    }


    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemImageHome: ImageView = itemView.findViewById(R.id.img_td_item_image)
        val itemNameHome: TextView = itemView.findViewById(R.id.txt_td_item_name)
        val itemPriceHome: TextView = itemView.findViewById(R.id.txt_td_item_price)
        val itemPaymentMode: TextView = itemView.findViewById(R.id.txt_payment_mode)
        val itemQuantity: TextView = itemView.findViewById(R.id.txt_td_quantity)

    }
}