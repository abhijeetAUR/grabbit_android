package com.example.grabbit.bnhome

import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class HomeBnActivity : AppCompatActivity(), AccountFragment.BtnLogoutClicked {

    override fun logoutApplication() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
        finish()
    }

    companion object {
        var qrCodeResult : String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_bn)
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
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.cart_bn -> {
                    cartFragment = CartFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, cartFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.account_bn -> {
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
