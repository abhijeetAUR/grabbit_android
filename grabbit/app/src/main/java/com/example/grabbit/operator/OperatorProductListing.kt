package com.example.grabbit.operator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.R
import com.example.grabbit.operator.adapter.OPProductListAdapter
import com.example.grabbit.operator.adapter.TrayListAdapter

class OperatorProductListing : AppCompatActivity() {

    val trayListAdapter : TrayListAdapter? = null
    val opProductListAdapter : OPProductListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_product_listing)
        //TODO : Added adapter for tray, intialize adapter here and assign it to recyclerview.
    }
}
