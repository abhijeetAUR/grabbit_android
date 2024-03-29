package com.example.grabbit.operator.opHomeBnActivity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.grabbit.R
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.operator.opAccount.OpAccount
import com.example.grabbit.operator.opHomeProductListing.OpHomeProduct
import com.example.grabbit.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class OPHomeBnActivity : AppCompatActivity(), OpAccount.BtnLogoutClicked {

    private lateinit var opHomeProduct: OpHomeProduct
    private lateinit var opAccount: OpAccount
    var sharedPreferences: SharedPreferences? = null
    var qrCodeResult: String? = null
    val singletonProductDataHolder = SingletonProductDataHolder.instance
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is OpAccount) {
            fragment.setOnClickListenerBtnLogoutClicked(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ophome_bn)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        qrCodeResult = intent.getStringExtra("name")
        putKioskIdInSharedPref()

        val bottomNavigationHome: BottomNavigationView =
            findViewById(R.id.op_bottom_navigation_home)
        opHomeProduct = OpHomeProduct()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.op_framelayout_bottomnavigation, opHomeProduct)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        bottomNavigationHome.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    opHomeProduct = OpHomeProduct()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.op_framelayout_bottomnavigation, opHomeProduct)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.navigation_account -> {
                    opAccount = OpAccount()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.op_framelayout_bottomnavigation, opAccount)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }
    }

    private fun putKioskIdInSharedPref() {
        if (qrCodeResult != null && qrCodeResult!!.isNotEmpty()) {
            val editor = sharedPreferences!!.edit()
            editor.putString(machineKioskId, qrCodeResult!!)
            editor.apply()
        }
    }

    override fun logoutApplication() {
        changedIsOperatorLoggedIntoFalse()
        clearDataSource()
        val intent = Intent(this, LoginActivity::class.java);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearDataSource() {
        singletonProductDataHolder.lstProductsAddedToCart.clear()
        singletonProductDataHolder.homeProductDictionary.clear()
        singletonProductDataHolder.lstBtnNameAndStatus.clear()
        singletonProductDataHolder.lstOfProductDispensed.clear()
        singletonProductDataHolder.opProductDictionary.clear()
    }


    private fun changedIsOperatorLoggedIntoFalse() {
        if (sharedPreferences != null) {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(isOperatorLoggedIn, false)
            editor.apply()
        }
    }

}
