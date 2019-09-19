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

        val service = LoginFactory.makeLoginService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getLoginResponse()
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //TODO: Update ui on response
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

        button.setOnClickListener {
            // Handler code here.
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }

        textView3.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }

    }
}
