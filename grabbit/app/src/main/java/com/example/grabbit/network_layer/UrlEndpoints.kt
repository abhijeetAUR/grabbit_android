package com.example.grabbit.network_layer

class UrlEndpoints {
    companion object {
        const val userLogin = "/Userdetails.asmx/USER_LOGIN"
        const val newRegistration = "/Userdetails.asmx/NEWREGISTRATION"
        const val paytm = "/generate_checksum"
        const val generateChecksum = "/GenerateChecksum.aspx"
        const val generateChecksumPhp = "/payment/payment_paytm/generateChecksum.php"
        const val productListHome = "/Userdetails.asmx/PRODUCTLIST"
        const val createInvoice = "/Userdetails.asmx/CreateInvoice"
        const val updateInvoice = "/Userdetails.asmx/UpdateInvoice"
        const val chargeWallet = "/USERDETAILS.asmx/RECHARGEWALLET"
        const val balanceUser = "/USERDETAILS.asmx/BALANCE_USER"
        const val transactionDetails = "/Userdetails.asmx/TRANSACTIONDETAILS"

        //OPERATOR
        const val productlistForLoading = "/Userdetails.asmx/PRODUCTLISTFORLOADING"
        const val operatorLogin = "/Userdetails.asmx/OPERATOR_LOGIN"
        const val operatorProductListLoadByOperator = "/USERDETAILS.asmx/PRODUCTLISTTOLOADBYOPERATOR"
    }
}