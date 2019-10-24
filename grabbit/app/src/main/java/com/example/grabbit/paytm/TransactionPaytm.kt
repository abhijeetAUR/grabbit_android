package com.example.grabbit.paytm

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.grabbit.R
import com.example.grabbit.constants.*
import com.example.grabbit.paytm.contract.PaytmFactory
import com.example.grabbit.paytm.contract.RechargeWalletFactory
import com.example.grabbit.paytm.model.PaytmTransactionRequest
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
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
        private var paramMap: HashMap<String, String> = HashMap()
        val service = PaytmFactory.makePaytmService()
        val rechargeWalletService = RechargeWalletFactory.makeRechargeWalletService()
        var sharedPreferences: SharedPreferences? = null
    }

    private var balanceDifference = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trasnaction_paytm)
        pg_bar_txn_paytm.visibility = View.VISIBLE
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        checkInternetConnection()
    }

    override fun onStart() {
        super.onStart()
        balanceDifference = intent.getIntExtra(BALANCE_DIFFERENCE, 0)
        print(balanceDifference)
    }


    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        callPaytm()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@TransactionPaytm)
                        runOnUiThread {
                            pg_bar_txn_paytm.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }


    private fun callPaytm() {
        val orderId = UUID.randomUUID().toString().subSequence(0, 12).toString().replace("-", "")
        val customerId = UUID.randomUUID().toString().subSequence(0, 12).toString().replace("-", "")
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getChecksum(
                    mid = M_ID,
                    orderId = orderId,
                    custId = customerId,
                    txnAmount = balanceDifference.toString(),
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
                                M_ID, orderId, customerId, CHANNEL_ID, balanceDifference.toString(),
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
        }catch (e: Exception){
            e.printStackTrace()
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
                ).show()
                finish()
                //TODO: Add wallet recharge api
                //checkInternetConnectionForWallet(inResponse)
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



    private fun checkInternetConnectionForWallet(inResponse: Bundle) {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        sendRechargeWalletToServer(inResponse = inResponse)
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@TransactionPaytm)
                        runOnUiThread {
                            pg_bar_txn_paytm.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun sendRechargeWalletToServer(inResponse: Bundle) {
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        CoroutineScope(Dispatchers.IO).launch {
            val response = rechargeWalletService.rechargeWallet(
                mobileNo = mobileNo.toString(),
                amount = inResponse.get("TXNAMOUNT").toString(),
                bankName = "",//inResponse.get("BANKNAME").toString(),
                orderId = inResponse.get("ORDERID").toString(),
                txnId = inResponse.get("TXNID").toString(),
                respCode = inResponse.get("RESPCODE").toString(),
                paymentMode = inResponse.get("PAYMENTMODE").toString(),
                bankTxtId = "",//inResponse.get("BANKTXNID").toString(),
                gateWayName = "",//inResponse.get("HDFC").toString(),
                respMsg = ""//inResponse.get("RESPMSG").toString()
            )
            withContext(Dispatchers.Main) {
                print(response)
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
