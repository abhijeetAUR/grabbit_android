package com.example.grabbit.paytm.contract
import com.example.grabbit.network_layer.PAYTM_BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints.Companion.generateChecksumPhp
import com.example.grabbit.paytm.model.PaytmChecksumResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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