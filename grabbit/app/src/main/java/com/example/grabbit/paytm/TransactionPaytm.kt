package com.example.grabbit.paytm

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.grabbit.R
import com.example.grabbit.constants.*
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.mobileNumber
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.activity_trasnaction_paytm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*
import kotlin.collections.HashMap

class TransactionPaytm : AppCompatActivity() {

    companion object {
        val amount = "11.00"
        private var paramMap: HashMap<String, String> = HashMap()
        val service = PaytmFactory.makePaytmService()
        var sharedPreferences: SharedPreferences? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trasnaction_paytm)
        pg_bar_txn_paytm.visibility = View.VISIBLE
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        callPaytm()
    }


    private fun callPaytm() {
        val orderId = UUID.randomUUID().toString().subSequence(0, 12).toString().replace("-", "")
        val customerId = UUID.randomUUID().toString().subSequence(0, 12).toString().replace("-", "")
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
                            WEBSITE, CALLBACK_URL, INDUSTRY_TYPE_ID
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
        pg_bar_txn_paytm.visibility = View.GONE
    }

    private fun initializePayment(checksum: String, request: PaytmTransactionRequest) {
        val service = PaytmPGService.getStagingService()
        //Use this for production environment
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
                    this@TransactionPaytm,
                    "UI Error " + inErrorMessage,
                    Toast.LENGTH_LONG
                ).show();
            }

            override fun onTransactionResponse(inResponse: Bundle) {
                Toast.makeText(
                    this@TransactionPaytm,
                    "Payment Transaction response " + inResponse.toString(),
                    Toast.LENGTH_LONG
                ).show();
                sendRechargeWalletToServer(inResponse)
            }

            fun sendRechargeWalletToServer(inResponse : Bundle) {
                if (sharedPreferences == null)
                    return
                val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
                CoroutineScope(Dispatchers.IO).launch {
                    service.rechargeWallet(
                        mobileNo = mobileNo.toString(),
                        amount = inResponse.get("TXNAMOUNT").toString(),
                        bankName = inResponse.get("BANKNAME").toString(),
                        orderId = inResponse.get("ORDERID").toString(),
                        txnId = inResponse.get("TXNID").toString(),
                        respCode = inResponse.get("RESPCODE").toString(),
                        paymentMode = inResponse.get("PAYMENTMODE").toString(),
                        bankTxtId = inResponse.get("BANKTXNID").toString(),
                        gateWayName = inResponse.get("HDFC").toString(),
                        respMsg = inResponse.get("RESPMSG").toString()
                    )
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            }

            override fun networkNotAvailable() {
                Toast.makeText(
                    this@TransactionPaytm,
                    "Network connection error: Check your internet connectivity",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }

            override fun clientAuthenticationFailed(inErrorMessage: String) {
                Toast.makeText(
                    this@TransactionPaytm,
                    "Authentication failed: Server error" + inErrorMessage.toString(),
                    Toast.LENGTH_LONG
                ).show();
                finish()
            }

            override fun onErrorLoadingWebPage(
                iniErrorCode: Int,
                inErrorMessage: String,
                inFailingUrl: String
            ) {
                Toast.makeText(
                    this@TransactionPaytm,
                    "Unable to load webpage " + inErrorMessage.toString(),
                    Toast.LENGTH_LONG
                ).show();
                finish()
            }

            override fun onBackPressedCancelTransaction() {
                Toast.makeText(this@TransactionPaytm, "Transaction cancelled", Toast.LENGTH_LONG)
                    .show();
                finish()
            }

            override fun onTransactionCancel(inErrorMessage: String, inResponse: Bundle) {
                Toast.makeText(this@TransactionPaytm, "Transaction cancelled", Toast.LENGTH_LONG)
                    .show();
                finish()
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
