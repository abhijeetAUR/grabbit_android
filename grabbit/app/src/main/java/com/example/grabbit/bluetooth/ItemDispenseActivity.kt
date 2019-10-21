package com.example.grabbit.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.grabbit.R
import com.example.grabbit.bnhome.HomeBnActivity
import com.example.grabbit.bnhome.bnhome.DispensedItemData
import com.example.grabbit.bnhome.bnhome.SingletonProductDataHolder
import com.example.grabbit.utils.AlertDialogBox
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.mobileNumber
import kotlinx.android.synthetic.main.activity_item_dispense.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ItemDispenseActivity : AppCompatActivity() {

    private var sendDataToMega = "12"
    private var itemsDispatched = 0
    private var itemsToDispatch = 0
    private var singletonProductDataHolder: SingletonProductDataHolder? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var receivedDataFromMega = 0
    private var counter = 0

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
        ConnectToDevice(this).execute()
    }

    override fun onBackPressed() {
        //TODO: Handle disconnection of bluetooth on back button pressed, uncomment super.onBackPressed()
        super.onBackPressed()
        disconnect()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        counter = 0
        itemsDispatched = 0
        itemsToDispatch = getItemDispatchCount()
        runOnUiThread {
            txt_item_dispense_msg.text =
                "Dispensing item : ${singletonProductDataHolder!!.lstProductsAddedToCart[counter].ITEMNAME}"
        }
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }


    fun writeDataToMega() {
        fixedRateTimer("default", false, 0L, 1000) {
            if (itemsDispatched == itemsToDispatch) {
                cancel()
//                sendDispenseDataInformation()
                clearDataInCart()
                navigateToHomePage()

            } else {
                sendData()
            }
        }
    }

    private fun sendDispenseDataInformation() {

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
                inputStream = mBluetoothSocket!!.inputStream
                if (inputStream != null && outputStream != null) {
                    var arr: ByteArray
                    when (receivedDataFromMega) {
                        48 -> {
                            var data =
                                singletonProductDataHolder!!.lstProductsAddedToCart[counter].SERIALDATA
                            val homeProduct = singletonProductDataHolder!!.lstProductsAddedToCart[counter]
                            singletonProductDataHolder!!.lstOfProductDispensed.add(DispensedItemData(status = true, data = homeProduct))
                            runOnUiThread {
                                txt_item_dispense_msg.text =
                                    "Dispensing item : ${singletonProductDataHolder!!.lstProductsAddedToCart[counter].ITEMNAME}"
                                counter += 1
                                itemsDispatched += 1
                            }
                            sendDataToMega = data
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
                    receivedDataFromMega = DataInputStream(inputStream).read()
                }
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
        if (inputStream != null) {
            try {
                inputStream!!.close()
            } catch (e: Exception) {
            }

            inputStream = null
        }

        if (outputStream != null) {
            try {
                outputStream!!.close()
            } catch (e: Exception) {
            }

            outputStream = null
        }

        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket!!.close()
                mIsConnected = false
            } catch (e: Exception) {
            }

            mBluetoothSocket = null
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