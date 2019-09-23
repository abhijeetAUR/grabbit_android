package com.example.grabbit.bnhome.bnhome


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.grabbit.R
import com.example.grabbit.home.Itemlist_Adapter
import com.example.grabbit.home.MenuAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_product_list.layoutManager = LinearLayoutManager(activity?.applicationContext, RecyclerView.HORIZONTAL, false)
        val categories : ArrayList<String> = ArrayList()
        for(i in 1..20){
            categories.add("Menu $i")
        }
        rv_product_list.adapter = MenuAdapter(categories)

        purchase_item_list.layoutManager = LinearLayoutManager(activity?.applicationContext)
        purchase_item_list.adapter = Itemlist_Adapter()
    }


}
