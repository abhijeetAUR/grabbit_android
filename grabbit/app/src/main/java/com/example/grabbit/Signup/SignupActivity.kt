package com.example.grabbit.Signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.R
import com.example.grabbit.login.LoginActivity
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SignupActivity : AppCompatActivity() {

    companion object{
        val service = SignupFactory.makeSignupService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btn_sign_up.setOnClickListener {
            // Handler code here.
            if (validateTextBox()){
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getSignupResponse(edit_text_username.toString(),edit_text_fullname.toString(),edit_text_contactno.toString(),edit_text_email.toString())
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                //TODO: Update ui on response
                                print(response.body())
                                if(response.body()!!.first().Result.contentEquals("Success")){
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
    }
    private fun validateTextBox(): Boolean{
        if (edit_text_username.text.isNotEmpty() && edit_text_email.text.isNotEmpty() && edit_text_fullname.text.isNotEmpty() && edit_text_contactno.text.isNotEmpty())
            return true
        return false
    }
}