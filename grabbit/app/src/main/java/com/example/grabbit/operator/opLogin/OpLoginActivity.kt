package com.example.grabbit.operator.opLogin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.HttpException
import com.example.grabbit.R
import com.example.grabbit.operator.opLogin.contract.OpLoginService
import com.example.grabbit.scanner.InformationActivity
import com.example.grabbit.scanner.QrScannerActivity
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.activity_op_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpLoginActivity : AppCompatActivity() {
    var sharedPreferences: SharedPreferences? = null
    companion object{
        val service = OpLoginService.makeOpLoginService()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_op_login)
        setup()
    }

    private fun setup(){
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        op_txt_back_arrow.setOnClickListener { finish() }
        op_progressBar.visibility = View.GONE
        op_btn_sign_in.setOnClickListener {
            op_progressBar.visibility = View.VISIBLE
            checkInternetConnection()
        }
        op_txtViewSignUp.setOnClickListener {
            //TODO: Call sign up activity here
        }
    }
    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        signInNetworkCall()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@OpLoginActivity)
                        runOnUiThread {
                            op_progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun signInNetworkCall() {
        if (validateTextBox()) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.postOperatorLoginDetails(
                        op_edit_text_username.text.toString().trim(),
                        op_edit_text_password.text.toString().trim()
                    )
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                //TODO: Send operator to direct scanner page
                                if (response.body()!!.first().Result.contentEquals("SUCCESS")) {
                                    putDataInSharedPreferences(response.body()!!.first().OPNAME)
                                    val intent =
                                        Intent(applicationContext, QrScannerActivity::class.java)
                                    startActivity(intent)
                                    op_progressBar.visibility = View.GONE
                                    finish()
                                } else if (response.body()!!.first().Result.contentEquals("FAILED")) {
                                    AlertDialogBox.showDialog(
                                        this@OpLoginActivity,
                                        title = "Validation failed",
                                        message = "Invalid contact number and password",
                                        btnText = "Ok",
                                        progressBar = op_progressBar
                                    )
                                }
                            } else {
                                print("Error: ${response.code()}")
                            }
                        } catch (e: HttpException) {
                            e.printStackTrace()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }
            }catch(e: Exception){
                e.printStackTrace()
            }

        }
    }

    private fun putDataInSharedPreferences(name: String) {
        if (sharedPreferences != null){
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(isOperatorLoggedIn, true)
            editor.putString(mobileNumber, op_edit_text_username.text.toString().trim())
            editor.putString(username, name)
            editor.apply()
        }
    }


    private fun validateTextBox(): Boolean {
        if (op_edit_text_password.text.isNotEmpty() && op_edit_text_username.text.isNotEmpty()) {
            if (isPhoneNumberValid(op_edit_text_username.text.trim().toString()))
                return true
            else
                AlertDialogBox.showDialog(
                    this@OpLoginActivity,
                    title = "Validation failed",
                    message = "Not a valid phone number",
                    btnText = "Ok",
                    progressBar = op_progressBar
                )
        } else {
            AlertDialogBox.showDialog(
                this@OpLoginActivity,
                title = "Validation failed",
                message = "All fields required",
                btnText = "Ok",
                progressBar = op_progressBar
            )
        }
        return false
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

}
