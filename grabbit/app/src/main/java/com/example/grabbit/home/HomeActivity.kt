package com.example.grabbit.home

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.category_view.*

class HomeActivity : AppCompatActivity() {

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        rv_product_list.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val categories : ArrayList<String> = ArrayList()
        for(i in 1..20){
            categories.add("Menu $i")
        }
        rv_product_list.adapter = MenuAdapter(categories)

        purchase_item_list.layoutManager = LinearLayoutManager(this)
        purchase_item_list.adapter = Itemlist_Adapter()

    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_recepies -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


}
