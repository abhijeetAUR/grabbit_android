package com.example.grabbit.scanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.MainActivity
import com.example.grabbit.R
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class QrScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler{

    private var mScannerView: ZBarScannerView? = null

    override fun handleResult(rawResult: Result?) {
        val name = rawResult!!.barcodeFormat.name.toString()
        val intent = Intent(this@QrScannerActivity,MainActivity::class.java )
        intent.putExtra("name", name)
        startActivity(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZBarScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)
    }
    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }
}
