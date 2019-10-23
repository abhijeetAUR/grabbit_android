package com.example.grabbit.bnhome.bncart


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.grabbit.R
import com.example.grabbit.bluetooth.ScanBluetoothDevices
import com.example.grabbit.bnhome.bnhome.DispensedItemData
import com.example.grabbit.bnhome.bnhome.SingletonProductDataHolder
import com.example.grabbit.utils.ConnectionDetector
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.mobileNumber
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.*
import retrofit2.HttpException

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment(), CartItemListAdapter.OnBtnRemoveClickListener {
    override fun onBtnRemoveClick(position: Int) {
        removeItemFromListOfCart(index = position)
    }
    var countToMatchLstProductDispensed = 0
    var adapter : CartItemListAdapter? = null

    companion object{
        val singletonProductDataHolder = SingletonProductDataHolder.instance
        var sharedPreferences: SharedPreferences? = null
        val requestCreateInvoice = CreateInvoiceService.makeInvoiceService()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        setupRecyclerView()
        showNoItemSelectedTextView()
        pb_cart.visibility = View.GONE
        btnCheckout.setOnClickListener {
            checkInternetConnection()
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

    private fun sendCreateInvoiceDataInRecursiveCall(){
        val itemAddedToCart = singletonProductDataHolder.lstProductsAddedToCart
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        if (itemAddedToCart .count() > 0 ){
            CoroutineScope(Dispatchers.IO).async {
                val response = requestCreateInvoice.getCreateInvoice(
                    kioskid = itemAddedToCart[countToMatchLstProductDispensed].KioskID,
                    itemname = itemAddedToCart[countToMatchLstProductDispensed].ITEMNAME,
                    itemid = itemAddedToCart[countToMatchLstProductDispensed].ITEMID.toString(),
                    amount = itemAddedToCart[countToMatchLstProductDispensed].ITEMRATE.toString(),
                    trayid = itemAddedToCart[countToMatchLstProductDispensed].TRAYID.toString(),
                    colnumber = itemAddedToCart [countToMatchLstProductDispensed].COLNUMBER.toString(),
                    mobileno = mobileNo.toString()
                )
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            //Append invoice id to singleton dispensed item data
                            if(response.body() != null && response.body()!!.first().Invoiceidfull.isNotEmpty()){
                                singletonProductDataHolder.lstOfProductDispensed.add(DispensedItemData(itemAddedToCart[countToMatchLstProductDispensed], false, response.body()!!.first().Invoiceidfull))
                            }



                            countToMatchLstProductDispensed += 1
                            if (countToMatchLstProductDispensed == singletonProductDataHolder.lstProductsAddedToCart.count()){
                                navigateToBluetoothPage()
                            } else{
                                sendCreateInvoiceDataInRecursiveCall()
                            }
                        } else {
//                            AlertDialogBox.showDialog(activity!!.applicationContext, "Error", "", "O", progressBar = progressBar)
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

    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        sendCreateInvoiceDataInRecursiveCall()
                    } else {
                        pb_cart.visibility = View.GONE
                        ConnectionDetector.showNoInternetConnectionDialog(context = activity!!)
                    }
                }
            }
        })
    }


}
