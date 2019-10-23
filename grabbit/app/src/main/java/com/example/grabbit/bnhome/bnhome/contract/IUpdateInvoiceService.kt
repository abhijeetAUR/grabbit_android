package com.example.grabbit.bnhome.bnhome.contract

import com.example.grabbit.bnhome.bnhome.model.UpdateInvoiceResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface IUpdateInvoiceService {
    @FormUrlEncoded
    @POST(UrlEndpoints.updateInvoice)
    suspend fun updateCreateInvoice(
        @Field("invoiceidnew") invoiceIdNew: String,
        @Field("itemname") itemName: String,
        @Field("itemid") itemId: String,
        @Field("qty") qty: String,
        @Field("amount") amount: String,
        @Field("trayid") trayId: String,
        @Field("COLNUMBER") colnumber: String,
        @Field("DISPENSED") dispensed: String
    ): Response<List<UpdateInvoiceResponse>>
}

object UpdateInvoiceService {
    fun makeInvoiceService(): IUpdateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IUpdateInvoiceService::class.java)
    }
}