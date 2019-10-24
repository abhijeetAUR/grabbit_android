package com.example.grabbit.bnhome.bncart


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.grabbit.R
import com.example.grabbit.bluetooth.ScanBluetoothDevices
import com.example.grabbit.bnhome.bncart.adapter.CartItemListAdapter
import com.example.grabbit.bnhome.bncart.contract.CreateInvoiceService
import com.example.grabbit.paytm.TransactionPaytm
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.*
import retrofit2.HttpException

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment(), CartItemListAdapter.OnBtnRemoveClickListener {

    var countToMatchLstProductDispensed = 0
    var adapter: CartItemListAdapter? = null
    private var totalCostOfItems = 0
    private var userBalance = 0
    private var balanceDifference = 0

    companion object {
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
        calculateWalletBalance()
        btnCheckout.setOnClickListener {
            if (userBalance < totalCostOfItems) {
                showDialog("Low balance", "Please add Rs $balanceDifference to continue transaction", "Ok", "Cancel")
            } else {
                checkInternetConnection()
            }
        }
    }

    private fun calculateWalletBalance(){
        totalCostOfItems = singletonProductDataHolder.lstProductsAddedToCart.map { it.ITEMRATE }
            .reduce { total, next -> total + next }
        userBalance = getWalletBalance().toInt()
        if (userBalance < totalCostOfItems){
            balanceDifference = totalCostOfItems - userBalance
        }
    }

    private fun showDialog(title: String, message: String, btnText: String, btnNegativeText: String){
        val dialogBuilder = AlertDialog.Builder(ContextThemeWrapper(activity!!, R.style.MaterialTheme))

        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(btnText) { dialog, _ ->
                val intent = Intent(activity, TransactionPaytm::class.java)
                intent.putExtra(BALANCE_DIFFERENCE, balanceDifference)
                startActivity(intent)
                dialog.dismiss()
            }.setNegativeButton(btnNegativeText){ dialog, _ ->
                dialog.dismiss()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
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

    private fun sendCreateInvoiceDataInRecursiveCall() {
        val itemAddedToCart = singletonProductDataHolder.lstProductsAddedToCart
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        if (itemAddedToCart.count() > 0) {
            CoroutineScope(Dispatchers.IO).async {
                val response = requestCreateInvoice.getCreateInvoice(
                    kioskid = itemAddedToCart[countToMatchLstProductDispensed].KioskID,
                    itemname = itemAddedToCart[countToMatchLstProductDispensed].ITEMNAME,
                    itemid = itemAddedToCart[countToMatchLstProductDispensed].ITEMID.toString(),
                    amount = itemAddedToCart[countToMatchLstProductDispensed].ITEMRATE.toString(),
                    trayid = itemAddedToCart[countToMatchLstProductDispensed].TRAYID.toString(),
                    colnumber = itemAddedToCart[countToMatchLstProductDispensed].COLNUMBER.toString(),
                    mobileno = mobileNo.toString()
                )
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            //Append invoice id to singleton dispensed item data
                            if (response.body() != null && response.body()!!.first().Invoiceidfull.isNotEmpty()) {
                                singletonProductDataHolder.lstOfProductDispensed.add(
                                    DispensedItemData(
                                        itemAddedToCart[countToMatchLstProductDispensed],
                                        false,
                                        response.body()!!.first().Invoiceidfull
                                    )
                                )
                            }



                            countToMatchLstProductDispensed += 1
                            if (countToMatchLstProductDispensed == singletonProductDataHolder.lstProductsAddedToCart.count()) {
                                navigateToBluetoothPage()
                            } else {
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


    //Delegate functions

    override fun onBtnRemoveClick(position: Int) {
        removeItemFromListOfCart(index = position)
        totalCostOfItems = singletonProductDataHolder.lstProductsAddedToCart.map { it.ITEMRATE }
            .reduce { total, next -> total + next }
    }

    //Helper functions

    private fun getWalletBalance(): Float = if (sharedPreferences != null) {
        sharedPreferences!!.getFloat(walletBalance, 0.toFloat())
    } else {
        0.toFloat()
    }

    private fun setupRecyclerView() {
        cart_item_listrv.layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = CartItemListAdapter(
            singletonProductDataHolder.lstProductsAddedToCart,
            activity!!.applicationContext
        )
        cart_item_listrv.adapter = adapter
        adapter!!.setOnClickListener(this)
    }

    private fun showNoItemSelectedTextView() {
        if (singletonProductDataHolder.lstProductsAddedToCart.count() < 1)
            txt_no_data_available.visibility = View.VISIBLE
        else
            txt_no_data_available.visibility = View.GONE

    }

    private fun navigateToBluetoothPage() {
        if (singletonProductDataHolder.lstProductsAddedToCart.isNotEmpty()) {
            val intent = Intent(activity, ScanBluetoothDevices::class.java)
            startActivity(intent)
        }
    }

    private fun removeItemFromListOfCart(index: Int) {
        singletonProductDataHolder.lstProductsAddedToCart.removeAt(index)
        adapter!!.notifyDataSetChanged()
        showNoItemSelectedTextView()
    }


}
