package com.example.grabbit.operator.opHomeBnActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.grabbit.R
import com.example.grabbit.operator.opHomeProductListing.OpHomeProduct
import com.google.android.material.bottomnavigation.BottomNavigationView

class OPHomeBnActivity : AppCompatActivity() {

    private lateinit var opHomeProduct : OpHomeProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ophome_bn)

        val bottomNavigationHome : BottomNavigationView = findViewById(R.id.op_bottom_navigation_home)
        opHomeProduct = OpHomeProduct()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.op_framelayout_bottomnavigation, opHomeProduct )
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        bottomNavigationHome.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.home_bn -> {
                    opHomeProduct = OpHomeProduct()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout_bottomnavigation, opHomeProduct)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.cart_bn -> {

                }
                R.id.account_bn -> {

                }

            }
            true
        }
    }
}
