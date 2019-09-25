package com.example.grabbit.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.grabbit.R
import kotlinx.android.synthetic.main.activity_scan_bluetooth_devices.*

class ScanBluetoothDevices : AppCompatActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    lateinit var mPairedDevices : Set<BluetoothDevice>
    private val requestEnableBluetooth= 1

    companion object{
        val EXTRA_ADDRESS : String = "20-18-08-34-F7-3C"
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
        select_device_referesh.setOnClickListener { pairedDeviceList() }
    }
    private fun pairedDeviceList(){
        mPairedDevices = mBluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        if (!mPairedDevices.isEmpty()){
            mPairedDevices.forEach {
                list.add(it)
                print(it)
            }
        } else {
            print("No paired bluetooth device found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.setOnItemClickListener { parent, view, position, id ->
            val device = list[position]
            val address: String = device.address

            val intent = Intent(this, ItemDispenseActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
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
