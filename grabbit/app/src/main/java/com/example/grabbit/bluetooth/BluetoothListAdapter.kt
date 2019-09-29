package com.example.grabbit.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R

class BluetoothListAdapter(private val list: ArrayList<BluetoothDevice>) :
    RecyclerView.Adapter<BluetoothListAdapter.CustomViewHolder>() {
    private var mOnBluetoothDispenseBtnClick: OnBluetoothDispenseBtnClick? = null

    interface OnBluetoothDispenseBtnClick {
        fun onDispenseBtnClick(position: Int)
    }

    fun setOnDispenseBtnClickListener(listener: OnBluetoothDispenseBtnClick) {
        mOnBluetoothDispenseBtnClick = listener
    }

    override fun getItemCount() = list.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val cellForRow = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_device_list, parent, false)
        return CustomViewHolder(cellForRow, mOnBluetoothDispenseBtnClick)
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.txtDeviceName.text = list[position].address.trim()
    }

    class CustomViewHolder(itemView: View, listener: OnBluetoothDispenseBtnClick?) :
        RecyclerView.ViewHolder(itemView) {
        val txtDeviceName: TextView = itemView.findViewById(R.id.txt_bluetooth_device_name)
        private val btnDispenseItem: ImageButton = itemView.findViewById(R.id.btn_dispense_item)

        init {
            btnDispenseItem.setOnClickListener {
                listener?.onDispenseBtnClick(adapterPosition)
            }
        }
    }
}