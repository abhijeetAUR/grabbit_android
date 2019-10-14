package com.example.grabbit.bnhome

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.ForwardingListener
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.grabbit.R
import com.example.grabbit.bnhome.bnaccount.AccountFragment
import com.example.grabbit.bnhome.bncart.CartFragment
import com.example.grabbit.bnhome.bnhome.HomeFragment
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.isUserLoggedIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home_bn.*
import kotlinx.android.synthetic.main.activity_main.*

class HomeBnActivity : AppCompatActivity(), AccountFragment.BtnLogoutClicked {

    override fun logoutApplication() {
        changedIsUserLoggedIntoFalse()
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
        finish()
    }

    companion object {
        var qrCodeResult : String? = null
        var sharedPreferences: SharedPreferences? = null

    }

    lateinit var homeFragment: HomeFragment
    lateinit var accountFragment: AccountFragment
    lateinit var cartFragment: CartFragment

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is AccountFragment) {
            fragment.setOnClickListenerBtnLogoutClicked(this)
        }
    }

    private fun changedIsUserLoggedIntoFalse(){
        if(sharedPreferences != null){
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(isUserLoggedIn, false)
            editor.apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_bn)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        qrCodeResult= intent.getStringExtra("name")

        val bottomNavigationHome : BottomNavigationView = findViewById(R.id.bottom_navigation_home)
        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framelayout_bottomnavigation, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        bottomNavigationHome.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.home_bn -> {
                    appbar_title.text="Home"
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.cart_bn -> {
                    appbar_title.text="Cart"
                    cartFragment = CartFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, cartFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.account_bn -> {
                    appbar_title.text="Account"
                    accountFragment = AccountFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, accountFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }
    }
}
