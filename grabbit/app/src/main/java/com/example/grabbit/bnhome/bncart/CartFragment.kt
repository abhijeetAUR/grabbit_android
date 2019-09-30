package com.example.grabbit.bnhome.bncart


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.grabbit.R
import com.example.grabbit.bluetooth.ScanBluetoothDevices
import com.example.grabbit.bnhome.bnhome.SingletonProductDataHolder
import com.example.grabbit.constants.*
import com.example.grabbit.paytm.PaytmFactory
import com.example.grabbit.paytm.PaytmTransactionRequest
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment(), CartItemListAdapter.OnBtnRemoveClickListener {
    override fun onBtnRemoveClick(position: Int) {
        removeItemFromListOfCart(index = position)
    }

    companion object{
        val singletonProductDataHolder = SingletonProductDataHolder.instance
    }
    var adapter : CartItemListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        showNoItemSelectedTextView()
        btnCheckout.setOnClickListener {

            navigateToBluetoothPage()
        }
    }

    private fun setupRecyclerView(){
        cart_item_listrv.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = CartItemListAdapter(singletonProductDataHolder.lstProductsAddedToCart, activity!!.applicationContext)
        cart_item_listrv.adapter = adapter
        adapter!!.setOnClickListener(this)
    }

    private fun showNoItemSelectedTextView(){
        if (singletonProductDataHolder.lstProductsAddedToCart.count() < 1)
            txt_no_data_available.visibility = View.VISIBLE
        else
            txt_no_data_available.visibility = View.GONE

    }

    private fun navigateToBluetoothPage(){
        if (singletonProductDataHolder.lstProductsAddedToCart.isNotEmpty()){
            val intent = Intent(activity, ScanBluetoothDevices::class.java)
            startActivity(intent)
        }
    }

    private fun removeItemFromListOfCart(index: Int){
        singletonProductDataHolder.lstProductsAddedToCart.removeAt(index)
        adapter!!.notifyDataSetChanged()
        showNoItemSelectedTextView()
    }


}
