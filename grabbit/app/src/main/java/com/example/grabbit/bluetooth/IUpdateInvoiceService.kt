package com.example.grabbit.bluetooth

import com.example.grabbit.network_layer.BASE_SECURE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface IUpdateInvoiceService {

    @POST(UrlEndpoints.updateInvoice)
    suspend fun updateCreateInvoice(
        @Query("invoiceidnew") invoiceIdNew: String,
        @Query("itemname") itemName: String,
        @Query("itemid") itemId: String,
        @Query("qty") qty: String,
        @Query("amount") amount: String,
        @Query("trayid") trayId: String,
        @Query("COLNUMBER") colnumber: String,
        @Query("DISPENSED") dispensed: String
    ): Response<List<UpdateInvoiceResponse>>
}

object UpdateInvoiceService {
    fun makeInvoiceService(): IUpdateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_SECURE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IUpdateInvoiceService::class.java)
    }
}