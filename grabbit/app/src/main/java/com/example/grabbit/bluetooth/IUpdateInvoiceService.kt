package com.example.grabbit.bluetooth

import com.example.grabbit.network_layer.BASE_SECURE_URL
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface IUpdateInvoiceService {
    @FormUrlEncoded
    @POST(UrlEndpoints.createInvoice)
    suspend fun getCreateInvoice(
        @Field("Kioskid") kioskid: String,
        @Field("invoiceid") invoiceid: String,
        @Field("itemname") itemname: String,
        @Field("itemid") itemid: String,
        @Field("amount") amount: String,
        @Field("trayid") trayid: String,
        @Field("COLNUMBER") colnumber: String,
        @Field("DISPENSED") dispensed: String,
        @Field("mobileno") mobileno: String): Response<List<UpdateInvoiceResponse>>
}

object UpdateInvoice{
    fun makeInvoiceService(): IUpdateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_SECURE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IUpdateInvoiceService::class.java)
    }
}