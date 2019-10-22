package com.example.grabbit.scanner

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grabbit.R
import com.example.grabbit.scanner.Adapter.TransactionDetailsAdapter
import com.example.grabbit.scanner.Model.TransactionDetailsResponse
import com.example.grabbit.scanner.Service.TransactionDetailsService
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.activity_transaction_details_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class TransactionDetailsPage : AppCompatActivity() {

    companion object{
        var transactionDetailsAdapter : TransactionDetailsAdapter? = null
        val items: ArrayList<TransactionDetailsResponse> = ArrayList()
        var sharedPreferences: SharedPreferences? = null
        val service = TransactionDetailsService.makeTransactionDetailsService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details_page)
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        transactionDetailsAdapter = TransactionDetailsAdapter(items, this)
        rv_transaction_details.layoutManager  = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rv_transaction_details.adapter = transactionDetailsAdapter
        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        fetchTransactionDetails()
                    } else {
                        pb_td.visibility = View.GONE
                        ConnectionDetector.showNoInternetConnectionDialog(context = applicationContext)
                    }
                }
            }
        })
    }

    private fun fetchTransactionDetails() {
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getTransactionDetails(mobileNo = "9890698284")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //TODO: Update ui on response
                        //Add to singleton class
                        if (response.body()!!.Table1.isNotEmpty()){
                            putDataInItems(response.body()!!.Table1)
                        } else{
                            AlertDialogBox.showDialog(
                                context = applicationContext,
                                progressBar = pb_td,
                                message = "No Details",
                                title = "No transaction has been done",
                                btnText = "Ok"
                            )
                        }

                        pb_td.visibility = View.GONE
                    } else {
                        print("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun putDataInItems(list: List<TransactionDetailsResponse>) {
        items.clear()
        items.addAll(list)
        transactionDetailsAdapter!!.notifyDataSetChanged()
    }



}
