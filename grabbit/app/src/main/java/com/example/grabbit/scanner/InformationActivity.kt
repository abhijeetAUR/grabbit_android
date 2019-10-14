package com.example.grabbit.scanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grabbit.R
import com.example.grabbit.paytm.TransactionPaytm
import kotlinx.android.synthetic.main.activity_information.*



class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        btn_scan_qr_code.setOnClickListener {
            startActivity(Intent(this@InformationActivity, QrScannerActivity::class.java))
        }
        btn_add_balance.setOnClickListener {
            startActivity(Intent(this@InformationActivity, TransactionPaytm::class.java))
        }
    }
}
