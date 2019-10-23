package com.example.grabbit.paytm.contract

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints.Companion.chargeWallet
import com.example.grabbit.paytm.model.WalletResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IRechargeWalletService {
    @FormUrlEncoded
    @POST(chargeWallet)
    suspend fun rechargeWallet(
        @Field("mobileno") mobileNo: String,
        @Field("amount") amount: String,
        @Field("bankname") bankName: String,
        @Field("orderid") orderId: String,
        @Field("txnid") txnId: String,
        @Field("respcode") respCode: String,
        @Field("paymentmode") paymentMode: String,
        @Field("banktxtid") bankTxtId: String,
        @Field("gatewayname") gateWayName: String,
        @Field("respmsg") respMsg: String
    ): WalletResponse
}

object RechargeWalletFactory {
    fun makeRechargeWalletService(): IRechargeWalletService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IRechargeWalletService::class.java)
    }
}