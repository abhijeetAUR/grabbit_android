package com.example.grabbit.operator.opProductDetailPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.grabbit.R
import com.example.grabbit.operator.opHomeProductListing.model.OPProductList
import kotlinx.android.synthetic.main.activity_op_product_detail_page.*

class OpProductDetailPage : AppCompatActivity() {
    private var product: OPProductList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_op_product_detail_page)
        product = intent.getSerializableExtra("HomeProduct") as OPProductList
        setupValues()
    }

    private fun setupValues(){
        if (product != null){
//            txt_kiosk_id.text = String.format(getString(R.string.KioskIdTemplate), product!!.KioskID)
            txt_product_name.text = String.format(getString(R.string.ProductName), product!!.ITEMNAME)
            Glide.with(this).load(product!!.ITEMIMAGE).into(img_product_detail)
        }

    }
}
