package com.example.grabbit.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.R
import kotlinx.android.synthetic.main.activity_item_dispense.*
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

class ItemDispenseActivity : AppCompatActivity() {

    companion object{
        var mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mBluetoothAdapter: BluetoothAdapter
        var mIsConnected: Boolean = false
        lateinit var mAddress: String
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var receivedDataFromMega = 0
        var sendDataToMega = "12"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_dispense)
        mAddress = intent.getStringExtra(ScanBluetoothDevices.EXTRA_ADDRESS)
        ConnectToDevice(this).execute()
        on.setOnClickListener {
            Timer().schedule(timerTask {
//                sendCommand("12")
                sendData()
            }, 20)
        }
        disconnect.setOnClickListener {
            disconnect()
        }
    }

    private fun sendData(){
        outputStream = mBluetoothSocket!!.outputStream
        var arr: ByteArray
        when(receivedDataFromMega){
            48 -> {
                sendDataToMega = "fi"
//                arr = byteArrayOf(sendDataToMega.toByte())
//                DataOutputStream(outputStream).write(arr)
                DataOutputStream(outputStream).writeBytes(sendDataToMega)
            }
            46,51 -> {
                sendDataToMega = "02"
                arr = byteArrayOf(sendDataToMega.toByte())
                DataOutputStream(outputStream).write(arr)
            }
            else -> {
                sendDataToMega = "12"
                arr = byteArrayOf(sendDataToMega.toByte())
                DataOutputStream(outputStream).write(arr)
            }
        }
        inputStream = mBluetoothSocket!!.inputStream
        receivedDataFromMega = DataInputStream(inputStream).read()
    }

//    private fun sendCommand(input : String){
//        var code = "12".toByte()
//        var status = true
//        var receivedDtata = 0
//        try {
//            outputStream = mBluetoothSocket!!.outputStream
//            while (true){
//                val arr = byteArrayOf(code)
//                if (receivedDtata == 48) {
//                    DataOutputStream(outputStream).writeBytes("fi")
//                } else{
//                    DataOutputStream(outputStream).write(arr)
//                }
//                inputStream = mBluetoothSocket!!.inputStream
//                receivedDtata = DataInputStream(inputStream).read()
//                if ((receivedDtata == 46 && status == true) || (receivedDtata == 51 && status == true)){
//                    status == false
//                    code = "02".toByte()
//                } else {
//                    code = "12".toByte()
//                }
//            }
//        }catch (e: IOException){
//            e.printStackTrace()
//        }
//    }

    private fun disconnect(){
        if (mBluetoothSocket != null){
            try {
                mBluetoothSocket!!.close()
                mBluetoothSocket = null
                mIsConnected = false
            } catch(e: Exception){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context): AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess){
                print("Not connected")
            } else {
                connectSuccess = true
                print("connected")
            }
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                if (mBluetoothSocket == null || !mIsConnected){
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress)
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mBluetoothSocket!!.connect()
                    print(mBluetoothSocket!!.isConnected)
                }
            }catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return ""
        }

    }
}
