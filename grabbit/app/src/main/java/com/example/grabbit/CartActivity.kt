package com.example.grabbit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabbit.home.CartItemList_Adapter
import com.paytm.pgsdk.PaytmPGService
import kotlinx.android.synthetic.main.activity_cart.*
import com.paytm.pgsdk.PaytmOrder
import com.example.grabbit.constants.*
import com.example.grabbit.paytm.PaytmFactory
import com.example.grabbit.paytm.PaytmTransactionRequest
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*
import kotlin.collections.HashMap


class CartActivity : AppCompatActivity() {
    companion object{
        private var paramMap: HashMap<String, String> = HashMap()
        val service = PaytmFactory.makePaytmService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        CartItemlistRv.layoutManager = LinearLayoutManager(this)
        CartItemlistRv.adapter = CartItemList_Adapter()


        CoroutineScope(Dispatchers.IO).launch {

        }


        btnCheckout.setOnClickListener {
            callPaytm()
        }
    }

    private fun callPaytm(){
        val request = PaytmTransactionRequest(M_ID,"ORDER00100981", "CUST0010991", CHANNEL_ID, "110.00",WEBSITE, CALLBACK_URL, INDUSTRY_TYPE_ID)
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
        service.startPaymentTransaction(this, true, true, paytmCallback())
    }

    fun paytmCallback(): PaytmPaymentTransactionCallback{
        return object : PaytmPaymentTransactionCallback {
            /*Call Backs*/
            override fun someUIErrorOccurred(inErrorMessage: String) {
                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
            }

            override fun onTransactionResponse(inResponse: Bundle) {
                Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

            }
            override fun networkNotAvailable() {
                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

            }
            override fun clientAuthenticationFailed(inErrorMessage: String) {
                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

            }
            override fun onErrorLoadingWebPage(
                iniErrorCode: Int,
                inErrorMessage: String,
                inFailingUrl: String
            ) {
                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
            }

            override fun onBackPressedCancelTransaction() {
                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
            }
            override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
            }
        }
    }
    private fun createParams(checksum: String,request: PaytmTransactionRequest): PaytmOrder{
        paramMap.put( "MID" , request.MID)
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , request.ORDER_ID)
        paramMap.put( "CUST_ID" , request.CUST_ID)
        paramMap.put( "CHANNEL_ID" , request.CHANNEL_ID)
        paramMap.put( "TXN_AMOUNT" , request.TXN_AMOUNT)
        paramMap.put( "WEBSITE" , request.WEBSITE)
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , request.INDUSTRY_TYPE_ID)
// This is the staging value. Production value is available in your dashboard
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

}
