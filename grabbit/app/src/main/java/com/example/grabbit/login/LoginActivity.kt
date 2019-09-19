package com.example.grabbit.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.R
import com.example.grabbit.SignupActivity
import com.example.grabbit.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        9890698284
//        grabbit123
        val service = LoginFactory.makeLoginService()


        btn_sign_in.setOnClickListener {
            // Handler code here.
            if (validateTextBox()){
                CoroutineScope(Dispatchers.IO).launch {
//                    val response = service.getLoginResponse(edit_text_username.text.toString(),edit_text_password.text.toString() )
                    val response = service.getLoginResponse("9890698284","grabbit123")
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                //TODO: Update ui on response
                                print(response.body())
                                if(response.body()!!.first().Result.contentEquals("SUCCESS")){
                                    val intent = Intent(applicationContext, HomeActivity::class.java)
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

        textView3.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateTextBox(): Boolean{
        if (edit_text_password.text.isNotEmpty() && edit_text_username.text.isNotEmpty())
            return true
        return false
    }
}
