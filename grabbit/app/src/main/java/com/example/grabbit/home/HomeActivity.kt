package com.example.grabbit.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.view.menu.MenuAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        rv_product_list.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val categories : ArrayList<String> = ArrayList()
        for(i in 1..20){
            categories.add("Menu $i")
        }
        rv_product_list.adapter = MenuAdapter(categories)
    }
}
