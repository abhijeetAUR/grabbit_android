package com.example.grabbit.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class AlertDialogBox {
    companion object{
        fun showDialog(context: Context, title: String, message: String, btnText: String){
            val dialogBuilder = AlertDialog.Builder(context)

            // set message of alert dialog
            dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(btnText, DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle(title)
            // show alert dialog
            alert.show()
        }
    }
}