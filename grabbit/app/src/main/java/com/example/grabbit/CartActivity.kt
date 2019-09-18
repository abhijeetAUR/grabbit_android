package com.example.grabbit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabbit.home.CartItemList_Adapter
import kotlinx.android.synthetic.main.activity_cart.*

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        CartItemlistRv.layoutManager = LinearLayoutManager(this)
        CartItemlistRv.adapter = CartItemList_Adapter()
    }
}
