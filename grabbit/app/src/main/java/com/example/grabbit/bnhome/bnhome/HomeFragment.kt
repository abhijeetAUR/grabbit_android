package com.example.grabbit.bnhome.bnhome


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.grabbit.R
import com.example.grabbit.bnhome.bncart.CartFragment.Companion.service
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    companion object{
        val Service = HomeFactory.makeHomeService()
    }

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

        //fetchProductListApi()
    }

    private fun fetchProductListApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = Service.getProductListService("00001")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //TODO: Update ui on response
                        print(response.body())

                    } else {
                        print("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}
