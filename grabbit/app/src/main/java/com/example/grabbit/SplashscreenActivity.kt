package com.example.grabbit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import com.example.grabbit.bnhome.HomeBnActivity
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.scanner.InformationActivity
import com.example.grabbit.scanner.QrScannerActivity
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.isUserLoggedIn

class SplashscreenActivity : AppCompatActivity() {
    val SPLASH_DELAY: Long = 3000 //3 seconds
    companion object{
        var sharedPreferences: SharedPreferences? = null

    }
    var hasUserLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        if(sharedPreferences != null){
            hasUserLoggedIn = sharedPreferences!!.getBoolean(isUserLoggedIn, false)
        }

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            if (hasUserLoggedIn){
                startActivity(Intent(this, InformationActivity::class.java))
            } else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, SPLASH_DELAY)
    }
}
