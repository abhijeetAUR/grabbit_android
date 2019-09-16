package com.example.grabbit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.grabbit.scanner.QrScannerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        var tvresult = String()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvresult.text = intent.getStringExtra("name")
        btn.setOnClickListener {
            val intent = Intent(this@MainActivity, QrScannerActivity::class.java)
            startActivity(intent)
        }
    }
}
