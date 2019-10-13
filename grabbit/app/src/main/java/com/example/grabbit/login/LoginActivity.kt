package com.example.grabbit.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.grabbit.R
import com.example.grabbit.Signup.SignupActivity
import com.example.grabbit.scanner.InformationActivity
import com.example.grabbit.utils.AlertDialogBox
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    companion object {
        val service = LoginFactory.makeLoginService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressBar.visibility = View.GONE
        btn_sign_in.setOnClickListener {
            // Handler code here.
            progressBar.visibility = View.VISIBLE
            checkInternetConnection()
        }

        txtViewSignUp.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signInNetworkCall() {
        if (validateTextBox()) {
            CoroutineScope(Dispatchers.IO).launch {
                                    val response = service.getLoginResponse(edit_text_username.text.toString().trim(),edit_text_password.text.toString().trim())
//                val response = service.getLoginResponse("9890698284", "grabbit123")
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            //TODO: Update ui on response
                            print(response.body())
                            if (response.body()!!.first().Result.contentEquals("SUCCESS")) {
                                val intent =
                                    Intent(applicationContext, InformationActivity::class.java)
                                startActivity(intent)
                                progressBar.visibility = View.GONE
                                finish()
                            } else if (response.body()!!.first().Result.contentEquals("FAILED")) {
                                AlertDialogBox.showDialog(
                                    this@LoginActivity,
                                    title = "Validation failed",
                                    message = "Invalid contact number and password",
                                    btnText = "Ok",
                                    progressBar = progressBar
                                )
                            }
                            //Do something with response e.g show to the UI.
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
        }
    }


    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        signInNetworkCall()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@LoginActivity)
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun validateTextBox(): Boolean {
        if (edit_text_password.text.isNotEmpty() && edit_text_username.text.isNotEmpty()){
            if (isPhoneNumberValid(edit_text_username.text.trim().toString()))
                return true
            else
                AlertDialogBox.showDialog(
                    this@LoginActivity,
                    title = "Validation failed",
                    message = "Not a valid phone number",
                    btnText = "Ok",
                    progressBar = progressBar
                )
        } else{
            AlertDialogBox.showDialog(
                this@LoginActivity,
                title = "Validation failed",
                message = "All fields required",
                btnText = "Ok",
                progressBar = progressBar
            )
        }
        return false
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }


}
