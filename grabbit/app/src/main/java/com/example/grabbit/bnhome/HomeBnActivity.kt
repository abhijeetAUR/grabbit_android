package com.example.grabbit.bnhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.grabbit.R
import com.example.grabbit.bnhome.bnaccount.AccountFragment
import com.example.grabbit.bnhome.bncart.CartFragment
import com.example.grabbit.bnhome.bnhome.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeBnActivity : AppCompatActivity() {

    lateinit var homeFragment: HomeFragment
    lateinit var accountFragment: AccountFragment
    lateinit var cartFragment: CartFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_bn)

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
