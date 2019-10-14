package com.example.grabbit.paytm

import com.example.grabbit.login.LoginResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.PAYTM_BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import com.example.grabbit.network_layer.UrlEndpoints.Companion.generateChecksum
import com.example.grabbit.network_layer.UrlEndpoints.Companion.generateChecksumPhp
import com.example.grabbit.network_layer.UrlEndpoints.Companion.paytm
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

//interface IPaytmService {
//    @FormUrlEncoded
//    @POST(paytm)
//    suspend fun getChecksum(@Field("MID")  mId: String,
//                            @Field("ORDER_ID")orderId: String ,
//                            @Field("CUST_ID") custId: String ,
//                            @Field("CHANNEL_ID")channelId: String,
//                            @Field("TXN_AMOUNT")  txnAmount : String,
//                            @Field("WEBSITE")website: String,
//                            @Field("CALLBACK_URL")callbackUrl: String,
//                            @Field("INDUSTRY_TYPE_ID")industryTypeId: String): PaytmChecksumResponse
//}

//interface IPaytmService {
//    @FormUrlEncoded
//    @POST(generateChecksum)
//    suspend fun getChecksum(
//        @Field("ORDER_ID") orderId: String,
//        @Field("TXN_AMOUNT") txnAmount: String
//    ): PaytmChecksumResponse
//}
//
//object PaytmFactory {
//    fun makePaytmService(): IPaytmService {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build().create(IPaytmService::class.java)
//    }
//}

interface IPaytmService {
    @FormUrlEncoded
    @POST(generateChecksumPhp)
    suspend fun getChecksum(
        @Field("MID") mid: String,
        @Field("ORDER_ID") orderId: String,
        @Field("CUST_ID") custId: String,
        @Field("CHANNEL_ID") channelId: String,
        @Field("TXN_AMOUNT") txnAmount: String,
        @Field("WEBSITE") website: String,
        @Field("CALLBACK_URL") callbackUrl: String,
        @Field("INDUSTRY_TYPE_ID") industryTypeId: String
    ): PaytmChecksumResponse
}

object PaytmFactory {
    fun makePaytmService(): IPaytmService {
        return Retrofit.Builder()
            .baseUrl(PAYTM_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IPaytmService::class.java)
    }
}