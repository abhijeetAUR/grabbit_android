package com.example.grabbit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabbit.home.CartItemList_Adapter
import com.paytm.pgsdk.PaytmPGService
import kotlinx.android.synthetic.main.activity_cart.*
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//http://localhost:3000/api/v1/paytm/initiatePayment

class CartActivity : AppCompatActivity() {
    companion object{
        private var paramMap: HashMap<String, String> = HashMap()
        lateinit var Order : PaytmOrder
        lateinit var service: PaytmPGService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        CartItemlistRv.layoutManager = LinearLayoutManager(this)
        CartItemlistRv.adapter = CartItemList_Adapter()


        CoroutineScope(Dispatchers.IO).launch {

        }


        btnCheckout.setOnClickListener {
            service = PaytmPGService.getStagingService()
            createParams()
            service.initialize(Order, null)
            val callback = object : PaytmPaymentTransactionCallback {
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
            service.startPaymentTransaction(
                this,
                true,
                true,
                callback
                )

        }
    }

    fun createParams(){
        paramMap.put( "MID" , "ElWbXY56998778382367");
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , "order1");
        paramMap.put( "CUST_ID" , "cust123");
        paramMap.put( "MOBILE_NO" , "7777777777");
        paramMap.put( "EMAIL" , "username@emailprovider.com");
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , "100.12");
        paramMap.put( "WEBSITE" , "WEBSTAGING");
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
        paramMap.put( "CHECKSUMHASH" , "w2QDRMgp1234567JEAPCIOmNgQvsi+BhpqijfM9KvFfRiPmGSt3Ddzw+oTaGCLneJwxFFq5mqTMwJXdQE2EzK4px2xruDqKZjHupz9yXev4=")
        Order = PaytmOrder(paramMap)
    }
}
