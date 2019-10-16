package com.example.grabbit.scanner

//{"Result":"SUCCESS","Balance":"380","KioskID":null,"UserID":null,"Fullname":null,
// "Username":null,"userTypeID":null,"ClientID":null}
data class GetWalletAmountResponse(
    val Result: String,
    val Balance: String,
    val KioskID: String,
    val UserID: String,
    val Fullname: String,
    val Username: String,
    val userTypeID: String
) {
}