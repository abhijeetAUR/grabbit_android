package com.example.grabbit.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.R
import com.example.grabbit.bnhome.HomeBnActivity
import com.example.grabbit.bnhome.bnhome.SingletonProductDataHolder
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer

class ItemDispenseActivity : AppCompatActivity() {

    private var sendDataToMega = "12"
    private var itemsDispatched = 0
    private var itemsToDispatch = 2
    private var singletonProductDataHolder: SingletonProductDataHolder? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var receivedDataFromMega = 0

    companion object {
        var mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mBluetoothAdapter: BluetoothAdapter
        var mIsConnected: Boolean = false
        lateinit var mAddress: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_dispense)
        mAddress = intent.getStringExtra(ScanBluetoothDevices.EXTRA_ADDRESS)
        singletonProductDataHolder = SingletonProductDataHolder.instance
        itemsToDispatch = getItemDispatchCount()
        ConnectToDevice(this).execute()
    }

    override fun onBackPressed() {
        //TODO: Handle disconnection of bluetooth on back button pressed, uncomment super.onBackPressed()
//        super.onBackPressed()
//        disconnect()
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }


    fun writeDataToMega() {
        fixedRateTimer("default", false, 0L, 1000) {
            if (itemsDispatched == itemsToDispatch) {
                cancel()
                sendDispenseDataInformation()
                clearDataInCart()
                navigateToHomePage()
            } else {
                sendData()
            }
        }
    }

    private fun sendDispenseDataInformation() {
        //TODO: send dispensed data information to web service
    }

    private fun clearDataInCart() {
        singletonProductDataHolder?.lstProductsAddedToCart?.clear()
    }

    private fun navigateToHomePage() {
        var intent = Intent(this@ItemDispenseActivity, HomeBnActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


    private fun sendData() {
        try {
            if (mBluetoothSocket != null) {
                outputStream = mBluetoothSocket!!.outputStream
                var arr: ByteArray
                when (receivedDataFromMega) {
                    48 -> {
                        var data = singletonProductDataHolder!!.lstProductsAddedToCart[itemsToDispatch].SERIALDATA
                        sendDataToMega = data
//                        sendDataToMega = "ai"
                        itemsDispatched += 1
                        DataOutputStream(outputStream).writeBytes(sendDataToMega)
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                sendDataToMega = "12"
                                arr = byteArrayOf(sendDataToMega.toByte())
                                DataOutputStream(outputStream).write(arr)
                                cancel()
                            }
                        }, 10000)
                    }
                    46, 51 -> {
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun getItemDispatchCount(): Int {
        if (singletonProductDataHolder != null) {
            return singletonProductDataHolder!!.lstProductsAddedToCart.count()
        }
        return 0
    }


    private fun disconnect() {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket!!.close()
                mBluetoothSocket = null
                mIsConnected = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private class ConnectToDevice(itemDispenseActivity: ItemDispenseActivity) :
        AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private var itemDispenseActivity: ItemDispenseActivity? = null

        init {
            this.itemDispenseActivity = itemDispenseActivity
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                print("Not connected")
            } else {
                connectSuccess = true
                itemDispenseActivity?.writeDataToMega()
            }
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                if (mBluetoothSocket == null || !mIsConnected) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress)
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mBluetoothSocket!!.connect()
                    print(mBluetoothSocket!!.isConnected)
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return ""
        }

    }
}