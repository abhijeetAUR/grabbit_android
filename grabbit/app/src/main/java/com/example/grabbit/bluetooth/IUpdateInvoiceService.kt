package com.example.grabbit.bluetooth

import com.example.grabbit.network_layer.BASE_SECURE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface IUpdateInvoiceService {
    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @POST(UrlEndpoints.createInvoice)
    suspend fun getCreateInvoice(
        @Field("Kioskid") kioskid: String,
        @Field("itemname") itemname: String,
        @Field("itemid") itemid: String,
        @Field("mobileno") mobileno: String,
        @Field("amount") amount: String,
        @Field("trayid") trayid: String,
        @Field("COLNUMBER") colnumber: String,
        @Field("DISPENSED") dispensed: String,
        @Field("invoiceid") invoiceid: String
    ): Response<List<UpdateInvoiceResponse>>
}

object UpdateInvoice {
    fun makeInvoiceService(): IUpdateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_SECURE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IUpdateInvoiceService::class.java)
    }
}