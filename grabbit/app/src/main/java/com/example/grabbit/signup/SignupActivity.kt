package com.example.grabbit.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.grabbit.R
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.utils.AlertDialogBox
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.edit_text_username
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SignupActivity : AppCompatActivity() {

    companion object {
        val service = SignupFactory.makeSignupService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        progressBarSignUp.visibility = View.GONE
        btn_sign_up.setOnClickListener {
            checkInternetConnection()
        }

        txt_back_arrow.setOnClickListener {
            finish()
        }

        txt_sing_in.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java ))
            finish()
        }

    }

    private fun signUpNetworkCall() {
        progressBarSignUp.visibility = View.VISIBLE
        if (validateTextBox()) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getSignupResponse(
                    edit_text_username.text.toString().trim(),
                    edit_text_fullname.text.toString().trim(),
                    edit_text_contactno.text.toString().trim(),
                    edit_text_email.text.toString().trim(),
                    ed_password.text.toString().trim()
                )
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            //TODO: Update ui on response
                            print(response.body())
                            progressBarSignUp.visibility = View.GONE
                            if (response.body()!!.first().Result.contentEquals("Success")) {
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(intent)
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
                        signUpNetworkCall()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@SignupActivity)
                    }
                }
            }
        })
    }


    private fun validateTextBox(): Boolean {
        if (edit_text_username.text.isNotEmpty() && edit_text_email.text.isNotEmpty() && edit_text_fullname.text.isNotEmpty() && edit_text_contactno.text.isNotEmpty()){
            if(isEmailValid(edit_text_email.text.trim().toString())){
                if (isPhoneNumberValid(edit_text_contactno.text.trim().toString()))
                    return true
                else
                    AlertDialogBox.showDialog(
                        this@SignupActivity,
                        title = "Validation failed",
                        message = "Not a valid phone number",
                        btnText = "Ok",
                        progressBar = progressBarSignUp
                    )
            } else{
                AlertDialogBox.showDialog(
                    this@SignupActivity,
                    title = "Validation failed",
                    message = "Not a valid email id",
                    btnText = "Ok",
                    progressBar = progressBarSignUp
                )
            }
        } else{
            AlertDialogBox.showDialog(
                this@SignupActivity,
                title = "Validation failed",
                message = "All fields required",
                btnText = "Ok",
                progressBar = progressBarSignUp
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
