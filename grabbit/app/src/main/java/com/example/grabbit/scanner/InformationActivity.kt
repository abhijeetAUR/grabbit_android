package com.example.grabbit.scanner

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.grabbit.R
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.paytm.TransactionPaytm
import com.example.grabbit.scanner.contract.BalanceWalletDetailsService
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
        checkInternetConnection()
        txt_transaction_details.setOnClickListener {
            startActivity(Intent(this@InformationActivity, TransactionDetailsPage::class.java))
        }
        btn_scan_qr_code.setOnClickListener {
            startActivity(Intent(this@InformationActivity, QrScannerActivity::class.java))
        }
        btn_add_balance.setOnClickListener {
            startActivity(Intent(this@InformationActivity, TransactionPaytm::class.java))
        }
        txt_ia_logout_btn.setOnClickListener {
            changedIsUserLoggedIntoFalse()
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent)
            finish()
        }
    }

    private fun changedIsUserLoggedIntoFalse(){
        if(sharedPreferences != null){
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(isUserLoggedIn, false)
            editor.apply()
        }
    }

    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        getWalletDetails()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@InformationActivity)
                        runOnUiThread {
                            progress_indicator.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun getWalletDetails() {
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        val name = sharedPreferences!!.getString(username, "User")
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.balanceWalletDetails(mobileNo.toString())
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.first().Balance.isNotEmpty()) {
                            val balance =
                                storeBalanceInSharedPreferences(response.body()!!.first().Balance.toFloat())
                            runOnUiThread {
                                txt_balance.text = balance.toString()
                                progress_indicator.visibility = View.GONE
                            }
                        } else {
                            runOnUiThread {
                                txt_balance.text = "0"
                                AlertDialogBox.showDialog(
                                    this@InformationActivity,
                                    "Add balance",
                                    "Please add balance before buying products",
                                    "Ok",
                                    progress_indicator
                                )
                            }
                        }
                        runOnUiThread {
                            txt_username.text = "Hi $name"
                        }

                    } else {
                        runOnUiThread {
                            AlertDialogBox.showDialog(
                                this@InformationActivity,
                                "Error",
                                "Unable to fetch balance",
                                "Ok",
                                progress_indicator
                            )
                            progress_indicator.visibility = View.GONE
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun storeBalanceInSharedPreferences(balance: Float): Float {
        val editor = sharedPreferences!!.edit()
        editor.putFloat(walletBalance, balance)
        editor.apply()
        return balance
    }

}
