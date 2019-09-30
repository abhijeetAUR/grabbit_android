package com.example.grabbit.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import kotlinx.android.synthetic.main.activity_scan_bluetooth_devices.*

class ScanBluetoothDevices : AppCompatActivity(), BluetoothListAdapter.OnBluetoothDispenseBtnClick {
    override fun onDispenseBtnClick(position: Int) {
            val device = list[position]
            val address: String = device.address
            val intent = Intent(this, ItemDispenseActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
            finish()
    }


    companion object{
        const private val requestEnableBluetooth= 1
        private var mBluetoothAdapter: BluetoothAdapter? = null
        lateinit var mPairedDevices : Set<BluetoothDevice>
        const val EXTRA_ADDRESS : String = "20-18-08-34-F7-3C"
        private var bluetoothListAdapter : BluetoothListAdapter? = null
        private var list : ArrayList<BluetoothDevice> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_bluetooth_devices)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null){
            print("this device does not support bluetooth")
        }
        if (!mBluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, requestEnableBluetooth)
        }
    }

    override fun onStart() {
        super.onStart()
        mPairedDevices = mBluetoothAdapter!!.bondedDevices
        list.clear()
        if (mPairedDevices.isNotEmpty()){
            mPairedDevices.forEach {
                list.add(it)
            }
        } else {
            print("No paired bluetooth device found")
        }
        rv_select_device_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bluetoothListAdapter  = BluetoothListAdapter(list)
        rv_select_device_list.adapter = bluetoothListAdapter
        bluetoothListAdapter!!.setOnDispenseBtnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestEnableBluetooth){
            if (resultCode == Activity.RESULT_OK){
                if (mBluetoothAdapter!!.isEnabled){
                    print("bluetooth Enabled")
                } else {
                    print("bluetooth disabled")
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                print("bluetooth enabling cancelled")
            }
        }
    }
}
