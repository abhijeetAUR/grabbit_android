package com.example.grabbit.scanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.bnhome.HomeBnActivity
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import android.Manifest.permission.CAMERA

import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.widget.Toast


class QrScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {
    private var mScannerView: ZBarScannerView? = null
    private val REQUEST_CODE_ASK_PERMISSIONS = 123


    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat
                .requestPermissions(
                    this@QrScannerActivity,
                    arrayOf(CAMERA),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(this@QrScannerActivity, "Permission granted", Toast.LENGTH_SHORT)
                    .show()
                mScannerView = ZBarScannerView(this)   // Programmatically initialize the scan
                setContentView(mScannerView)// ner view
            } else {
                // Permission Denied
                Toast.makeText(
                    this@QrScannerActivity,
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()

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

    override fun handleResult(rawResult: Result?) {
        val name = rawResult!!.barcodeFormat.name.toString()
        val intent = Intent(this@QrScannerActivity, HomeBnActivity::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}
