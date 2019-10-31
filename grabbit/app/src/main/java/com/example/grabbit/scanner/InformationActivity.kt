package com.example.grabbit.scanner

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
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
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import androidx.appcompat.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.FrameLayout


class InformationActivity : AppCompatActivity() {

    companion object {
        val service = BalanceWalletDetailsService.makeInvoiceService()
        var sharedPreferences: SharedPreferences? = null
    }

    var isOperatorLogIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        progress_indicator.visibility = View.VISIBLE

        isOperatorLogIn = sharedPreferences!!.getBoolean(isOperatorLoggedIn, false)

        if (isOperatorLogIn) {
            setupOperatorView()
        } else {
            checkInternetConnection()
        }

        txt_transaction_details.setOnClickListener {
            startActivity(Intent(this@InformationActivity, TransactionDetailsPage::class.java))
        }
        btn_scan_qr_code.setOnClickListener {
            startActivity(Intent(this@InformationActivity, QrScannerActivity::class.java))
        }
        btn_add_balance.setOnClickListener {
            buildCustomDialog()
        }
        txt_ia_logout_btn.setOnClickListener {
            showDialogForLogout("Confirm", "Are you sure you want to logout?", "Yes", "Cancel")
        }
    }

    private fun setupOperatorView() {
        val name = sharedPreferences!!.getString(username, "User")
        runOnUiThread {
            progress_indicator.visibility = View.GONE
            linear_ly_1.visibility = View.GONE
            linear_ly_2.visibility = View.GONE
            txt_username.text = "Hi $name"
        }

    }

    private fun buildCustomDialog() {
        val alert = AlertDialog.Builder(this)
//        val edittext = EditText(this)
//        edittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
//        alert.setMessage("Enter amount to recharge")
//        alert.setTitle("Recharge wallet")
//
//        alert.setView(edittext)

        val input = EditText(this)
        input.setSingleLine()
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        input.layoutParams = params
        container.addView(input)
        alert.setMessage("Enter amount to recharge")
        alert.setTitle("Recharge wallet")
        alert.setView(container)
        alert.setPositiveButton(
            "Continue"
        ) { dialog, whichButton ->
            val rechargeAmount = input.text.toString().toInt()
            val intent = Intent(this@InformationActivity, TransactionPaytm::class.java)
            intent.putExtra(BALANCE_DIFFERENCE, rechargeAmount)
            startActivity(intent)
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }

    private fun changedIsUserLoggedIntoFalse() {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(isUserLoggedIn, false)
            editor.putBoolean(isOperatorLoggedIn, false)
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

    private fun showDialogForLogout(
        title: String,
        message: String,
        btnText: String,
        btnNegativeText: String
    ) {
        val dialogBuilder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.MaterialTheme))
        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(btnText) { dialog, _ ->
                changedIsUserLoggedIntoFalse()
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent)
                dialog.dismiss()
                finish()
            }.setNegativeButton(btnNegativeText) { dialog, _ ->
                dialog.dismiss()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
    }


}
