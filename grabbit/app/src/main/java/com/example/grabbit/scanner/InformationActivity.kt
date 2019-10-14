package com.example.grabbit.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grabbit.R
import com.example.grabbit.constants.*
import com.example.grabbit.paytm.PaytmFactory
import com.example.grabbit.paytm.PaytmTransactionRequest
import com.example.grabbit.paytm.TransactionPaytm
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
        val amount = "110.00"
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
        val orderId = UUID.randomUUID().toString().subSequence(0,12).toString().replace("-","")
        val customerId = UUID.randomUUID().toString().subSequence(0,12).toString().replace("-","")
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.getChecksum(orderId, amount)
//            withContext(Dispatchers.Main) {
//                try {
//                    if (response.CHECKSUMHASH.isNotEmpty()) {
//                        //TODO: Update ui on response
//                        print(response.CHECKSUMHASH)
//                        val request = PaytmTransactionRequest(
//                            M_ID, orderId, customerId, CHANNEL_ID, amount,
//                            WEBSITE, CALLBACK_URL+orderId, INDUSTRY_TYPE_ID
//                        )
//                        initializePayment(response.CHECKSUMHASH, request)
//                    } else {
//                        print("Error: $response")
//                    }
//                } catch (e: HttpException) {
//                    e.printStackTrace()
//                } catch (e: Throwable) {
//                    e.printStackTrace()
//                }
//            }
//        }
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getChecksum(
                mid = M_ID,
                orderId = orderId,
                custId = customerId,
                txnAmount = amount,
                callbackUrl = CALLBACK_URL,
                website = WEBSITE,
                industryTypeId = INDUSTRY_TYPE_ID,
                channelId = CHANNEL_ID
            )
            withContext(Dispatchers.Main) {
                try {
                    if (response.CHECKSUMHASH.isNotEmpty()) {
                        //TODO: Update ui on response
                        print(response.CHECKSUMHASH)
                        val request = PaytmTransactionRequest(
                            M_ID, orderId, customerId, CHANNEL_ID, amount,
                            WEBSITE, CALLBACK_URL , INDUSTRY_TYPE_ID
                        )
                        initializePayment(response.CHECKSUMHASH, request)
                    } else {
                        print("Error: $response")
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
        val service = PaytmPGService.getStagingService()
//        val service = PaytmPGService.getProductionService()
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
        paramMap["MID"] = request.MID
        paramMap["ORDER_ID"] = request.ORDER_ID
        paramMap["CUST_ID"] = request.CUST_ID
        paramMap["CHANNEL_ID"] = request.CHANNEL_ID
        paramMap["TXN_AMOUNT"] = request.TXN_AMOUNT
        paramMap["WEBSITE"] = request.WEBSITE
        paramMap["INDUSTRY_TYPE_ID"] = request.INDUSTRY_TYPE_ID
        paramMap["CALLBACK_URL"] = request.CALLBACK_URL
        paramMap["CHECKSUMHASH"] = checksum
        return PaytmOrder(paramMap)
    }
}
