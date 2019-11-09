package com.example.grabbit.scanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grabbit.bnhome.HomeBnActivity
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.SharedPreferences

import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.grabbit.operator.opHomeBnActivity.OPHomeBnActivity
import com.example.grabbit.utils.*


class QrScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private val REQUEST_CODE_ASK_PERMISSIONS = 123
    var sharedPreferences: SharedPreferences? = null
    var hasUserLoggedIn = false
    var hasOperatorLoggedIn = false

    companion object {
        private var mScannerView: ZBarScannerView? = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (hasOperatorLoggedIn) {
            finish()
        }
    }

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
        } else {
            mScannerView = ZBarScannerView(this)   // Programmatically initialize the scan
            setContentView(mScannerView)// ner view
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
        sharedPreferences = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        hasUserLoggedIn = sharedPreferences!!.getBoolean(isUserLoggedIn, false)
        hasOperatorLoggedIn = sharedPreferences!!.getBoolean(isOperatorLoggedIn, false)
        if (mScannerView != null) {
            mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
            mScannerView!!.startCamera()          // Start camera on resume
        }
    }

    public override fun onPause() {
        super.onPause()
        if(mScannerView != null){
            mScannerView!!.stopCamera()    // Stop camera on pause
        }
    }

    override fun handleResult(rawResult: Result?) {
        val name = rawResult!!.contents.toString()
        if (name.contains("GRA")){
            if (hasUserLoggedIn) {
                val intent = Intent(this@QrScannerActivity, HomeBnActivity::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
            } else if (hasOperatorLoggedIn) {
                val intent = Intent(this@QrScannerActivity, OPHomeBnActivity::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
            }
        } else {
            showDialogQRCode(this, "Invalid qr code", "Please rescan qr code", "Ok")
        }
    }

    fun showDialogQRCode(context: Context, title: String, message: String, btnText: String){
        val dialogBuilder = AlertDialog.Builder(context)

        // set message of alert dialog
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(btnText) { dialog, _ ->
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
