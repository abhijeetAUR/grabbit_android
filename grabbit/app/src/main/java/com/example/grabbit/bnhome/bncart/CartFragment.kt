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
        private var paramMap: HashMap<String, String> = HashMap()
        val service = PaytmFactory.makePaytmService()
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
//            callPaytm()
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

    private fun callPaytm(){
        val request = PaytmTransactionRequest(
            M_ID,"ORDER00100981", "CUST0010991", CHANNEL_ID, "110.00",
            WEBSITE, CALLBACK_URL, INDUSTRY_TYPE_ID
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getChecksum(request.MID,request.ORDER_ID,request.CUST_ID , request.CHANNEL_ID, request.TXN_AMOUNT,request.WEBSITE, CALLBACK_URL, request.INDUSTRY_TYPE_ID)
            withContext(Dispatchers.Main) {
                try {
                    if (response.CHECKSUMHASH.isNotEmpty()) {
                        //TODO: Update ui on response
                        print(response.CHECKSUMHASH)
                        initializePayment(response.CHECKSUMHASH, request)
                    } else {
                        print("Error: ${response}")
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun initializePayment(checksum: String, request: PaytmTransactionRequest){
        val service = PaytmPGService.getStagingService();
        val order = createParams(checksum,request)
        service.initialize(order, null)
        service.startPaymentTransaction(activity?.applicationContext, true, true, paytmCallback())
    }

    fun paytmCallback(): PaytmPaymentTransactionCallback {
        return object : PaytmPaymentTransactionCallback {
            /*Call Backs*/
            override fun someUIErrorOccurred(inErrorMessage: String) {
                Toast.makeText(activity?.applicationContext, "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
            }

            override fun onTransactionResponse(inResponse: Bundle) {
                Toast.makeText(activity?.applicationContext, "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

            }
            override fun networkNotAvailable() {
                Toast.makeText(activity?.applicationContext, "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

            }
            override fun clientAuthenticationFailed(inErrorMessage: String) {
                Toast.makeText(activity?.applicationContext, "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

            }
            override fun onErrorLoadingWebPage(
                iniErrorCode: Int,
                inErrorMessage: String,
                inFailingUrl: String
            ) {
                Toast.makeText(activity?.applicationContext, "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
            }

            override fun onBackPressedCancelTransaction() {
                Toast.makeText(activity?.applicationContext, "Transaction cancelled" , Toast.LENGTH_LONG).show();
            }
            override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
            }
        }
    }
    private fun createParams(checksum: String,request: PaytmTransactionRequest): PaytmOrder {
        paramMap.put( "MID" , request.MID)
// Key ig and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , request.ORDER_ID)
        paramMap.put( "CUST_ID" , request.CUST_ID)
        paramMap.put( "CHANNEL_ID" , request.CHANNEL_ID)
        paramMap.put( "TXN_AMOUNT" , request.TXN_AMOUNT)
        paramMap.put( "WEBSITE" , request.WEBSITE)
// This g value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , request.INDUSTRY_TYPE_ID)
// This g value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", request.CALLBACK_URL)
        paramMap.put( "CHECKSUMHASH" , checksum)
        val order: PaytmOrder = PaytmOrder(paramMap)
        return order
    }

    private fun generateString() : String{
        var str = ""
        str = UUID.randomUUID().toString().replace("-", "")
        return str
    }

    private fun removeItemFromListOfCart(index: Int){
        singletonProductDataHolder.lstProductsAddedToCart.removeAt(index)
        adapter!!.notifyDataSetChanged()
        showNoItemSelectedTextView()
    }


}
