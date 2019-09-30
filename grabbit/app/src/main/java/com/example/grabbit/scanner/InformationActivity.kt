package com.example.grabbit.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grabbit.R
import com.example.grabbit.constants.*
import com.example.grabbit.paytm.PaytmFactory
import com.example.grabbit.paytm.PaytmTransactionRequest
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*
import kotlin.collections.HashMap


class InformationActivity : AppCompatActivity() {

    companion object {
        private var paramMap: HashMap<String, String> = HashMap()
        val service = PaytmFactory.makePaytmService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        btn_scan_qr_code.setOnClickListener {
            startActivity(Intent(this@InformationActivity, QrScannerActivity::class.java))
        }
        btn_add_balance.setOnClickListener {
            callPaytm()
        }
    }

    private fun callPaytm() {
//        val request = PaytmTransactionRequest(
//            M_ID,"ORDER00100981", "CUST0010991", CHANNEL_ID, "110.00",
//            WEBSITE, CALLBACK_URL, INDUSTRY_TYPE_ID
//        )

//        val request = PaytmTransactionRequest(
//            "ORDER00100981", "110.00"
//        )

        CoroutineScope(Dispatchers.IO).launch {
            //            val response = service.getChecksum(request.MID,request.ORDER_ID,request.CUST_ID , request.CHANNEL_ID, request.TXN_AMOUNT,request.WEBSITE, CALLBACK_URL, request.INDUSTRY_TYPE_ID)
            val response = service.getChecksum("ORDER00100981", "110.00")
            withContext(Dispatchers.Main) {
                try {
                    if (response.CHECKSUMHASH.isNotEmpty()) {
                        //TODO: Update ui on response
                        print(response.CHECKSUMHASH)
                        val request = PaytmTransactionRequest(
                            M_ID, "ORDER00100981", "CUST0010991", CHANNEL_ID, "110.00",
                            WEBSITE, CALLBACK_URL, INDUSTRY_TYPE_ID
                        )
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

    private fun initializePayment(checksum: String, request: PaytmTransactionRequest) {
        val service = PaytmPGService.getStagingService();
        val order = createParams(checksum, request)
        service.initialize(order, null)
        service.startPaymentTransaction(this, true, true, paytmCallback())
    }

    private fun paytmCallback(): PaytmPaymentTransactionCallback {
        return object : PaytmPaymentTransactionCallback {
            /*Call Backs*/
            override fun someUIErrorOccurred(inErrorMessage: String) {
                Toast.makeText(
                    this@InformationActivity,
                    "UI Error " + inErrorMessage,
                    Toast.LENGTH_LONG
                ).show();
            }

            override fun onTransactionResponse(inResponse: Bundle) {
                Toast.makeText(
                    this@InformationActivity,
                    "Payment Transaction response " + inResponse.toString(),
                    Toast.LENGTH_LONG
                ).show();

            }

            override fun networkNotAvailable() {
                Toast.makeText(
                    this@InformationActivity,
                    "Network connection error: Check your internet connectivity",
                    Toast.LENGTH_LONG
                ).show();

            }

            override fun clientAuthenticationFailed(inErrorMessage: String) {
                Toast.makeText(
                    this@InformationActivity,
                    "Authentication failed: Server error" + inErrorMessage.toString(),
                    Toast.LENGTH_LONG
                ).show();

            }

            override fun onErrorLoadingWebPage(
                iniErrorCode: Int,
                inErrorMessage: String,
                inFailingUrl: String
            ) {
                Toast.makeText(
                    this@InformationActivity,
                    "Unable to load webpage " + inErrorMessage.toString(),
                    Toast.LENGTH_LONG
                ).show();
            }

            override fun onBackPressedCancelTransaction() {
                Toast.makeText(this@InformationActivity, "Transaction cancelled", Toast.LENGTH_LONG)
                    .show();
            }

            override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
            }
        }
    }

    private fun createParams(checksum: String, request: PaytmTransactionRequest): PaytmOrder {
        paramMap.put("MID", request.MID)
// Key ig and production MID available in your dashboard
        paramMap.put("ORDER_ID", request.ORDER_ID)
        paramMap.put("CUST_ID", request.CUST_ID)
        paramMap.put("CHANNEL_ID", request.CHANNEL_ID)
        paramMap.put("TXN_AMOUNT", request.TXN_AMOUNT)
        paramMap.put("WEBSITE", request.WEBSITE)
// This g value. Production value is available in your dashboard
        paramMap.put("INDUSTRY_TYPE_ID", request.INDUSTRY_TYPE_ID)
// This g value. Production value is available in your dashboard
        paramMap.put("CALLBACK_URL", request.CALLBACK_URL)
        paramMap.put("CHECKSUMHASH", checksum)
        val order: PaytmOrder = PaytmOrder(paramMap)
        return order
    }

    private fun generateString(): String {
        var str = ""
        str = UUID.randomUUID().toString().replace("-", "")
        return str
    }


}
