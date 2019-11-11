package com.example.grabbit.operator.opProductDetailPage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.grabbit.R
import com.example.grabbit.operator.opHomeProductListing.model.OPProductList
import com.example.grabbit.operator.opProductDetailPage.contract.ProductLoadFactory
import com.example.grabbit.utils.*
import com.example.grabbit.utils.ConnectionDetector
import kotlinx.android.synthetic.main.activity_op_product_detail_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OpProductDetailPage : AppCompatActivity() {
    private var product: OPProductList? = null
    private val service = ProductLoadFactory.makeLoadService()
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_op_product_detail_page)
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        product = intent.getSerializableExtra("HomeProduct") as OPProductList
        setupValues()
        btn_load_data.setOnClickListener {
            checkInternetConnection()
        }
    }

    private fun setupValues() {
        if (product != null) {
//            txt_kiosk_id.text = String.format(getString(R.string.KioskIdTemplate), product!!.KioskID)
            txt_product_name.text =
                String.format(getString(R.string.ProductName), product!!.ITEMNAME)
            Glide.with(this).load(product!!.ITEMIMAGE).into(img_product_detail)
        }

    }


    private fun checkInternetConnection() {
        ConnectionDetector(object : ConnectionDetector.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet != null) {
                    if (internet) {
                        runOnUiThread {
                            pb_product_detail_page.visibility = View.VISIBLE
                        }
                        productListLoadByOperator()
                    } else {
                        ConnectionDetector.showNoInternetConnectionDialog(context = this@OpProductDetailPage)
                        runOnUiThread {
                            pb_product_detail_page.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun productListLoadByOperator() {
        val mobileNo = sharedPreferences!!.getString(mobileNumber, "0000000000")
        if (product != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.loadProductService(
                    mobileNo.toString(),
//                    "12345",
                    "00001",//TODO: Append KioskId in product object
//                        product!!.KioskID,
                    colNumber = ed_column_number.text.toString(),
                    trayId = ed_tray_id.text.toString(),
                    itemId = product!!.ITEMID.toString()
                )
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            //TODO: Update ui on response
                            pb_product_detail_page.visibility = View.GONE
                            print(response.body())
                            if (response.body()!!.first().Result.contentEquals("SUCCESS")) {
                                showDialog(this@OpProductDetailPage,
                                    title = "Success",
                                    message = "Product details updated successfully.",
                                    btnText = "Ok",
                                    progressBar = pb_product_detail_page)
                            } else if (response.body()!!.first().Result.contentEquals("FAILED")) {
                                AlertDialogBox.showDialog(
                                    this@OpProductDetailPage,
                                    title = "Validation failed",
                                    message = "Invalid tray id or column number",
                                    btnText = "Ok",
                                    progressBar = pb_product_detail_page
                                )
                            }
                            //Do something with response e.g show to the UI.
                        } else {
                            AlertDialogBox.showDialog(
                                this@OpProductDetailPage,
                                title = "Failure",
                                message = "Failed to upload product detail",
                                btnText = "Ok",
                                progressBar = pb_product_detail_page
                            )
                        }
                        runOnUiThread {
                            pb_product_detail_page.visibility = View.GONE
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

    fun showDialog(context: Context, title: String, message: String, btnText: String, progressBar: ProgressBar){
        val dialogBuilder = AlertDialog.Builder(context)

        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(btnText) { dialog, _ ->
                progressBar.visibility = View.GONE
                dialog.dismiss()
                finish()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
    }
}

