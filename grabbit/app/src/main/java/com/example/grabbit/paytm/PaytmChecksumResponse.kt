package com.example.grabbit.paytm

data class PaytmChecksumResponse(val CHECKSUMHASH: String,
                                 val ORDER_ID: String,
                                 val payt_STATUS: String){
}

data class WalletResponse(val Result: String){
}

