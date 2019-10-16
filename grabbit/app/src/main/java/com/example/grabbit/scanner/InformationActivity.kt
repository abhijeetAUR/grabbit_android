package com.example.grabbit.scanner

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.grabbit.R
import com.example.grabbit.paytm.TransactionPaytm
import com.example.grabbit.utils.*
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class InformationActivity : AppCompatActivity() {

    companion object {
        val service = BalanceWalletDetailsService.makeInvoiceService()
        var sharedPreferences: SharedPreferences? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        progress_indicator.visibility = View.VISIBLE
        getWalletDetails()
    }

    private fun getWalletDetails() {
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        val name= sharedPreferences!!.getString(username, "User")
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.balanceWalletDetails(mobileNo.toString())
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {

                        val balance = storeBalanceInSharedPreferences(response.body()!!.first().Balance.toFloat())
                        runOnUiThread {
                            txt_balance.text = balance.toString()
                            txt_username.text = "Hi $name"
                        }
                        if (balance < 1){
                            AlertDialogBox.showDialog(this@InformationActivity, "Add money", "Please add money before starting transaction", "Ok", progress_indicator)
                        } else{
                            btn_scan_qr_code.setOnClickListener {
                                startActivity(Intent(this@InformationActivity, QrScannerActivity::class.java))
                            }
                            btn_add_balance.setOnClickListener {
                                startActivity(Intent(this@InformationActivity, TransactionPaytm::class.java))
                            }
                        }
                    } else{
                        AlertDialogBox.showDialog(this@InformationActivity, "Error", "Unable to fetch balance", "Ok", progress_indicator)
                    }
                    progress_indicator.visibility = View.GONE
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun storeBalanceInSharedPreferences(balance: Float): Float{
        val editor = sharedPreferences!!.edit()
        editor.putFloat(walletBalance, balance)
        editor.apply()
        return balance
    }

}
