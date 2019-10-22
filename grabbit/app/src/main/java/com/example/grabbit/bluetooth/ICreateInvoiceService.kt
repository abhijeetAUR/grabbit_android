package com.example.grabbit.bluetooth

import com.example.grabbit.network_layer.BASE_SECURE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ICreateInvoiceService {
    @POST(UrlEndpoints.createInvoice)
    suspend fun getCreateInvoice(
        @Query("mobileno") mobileno: String,
        @Query("Kioskid") kioskid: String,
        @Query("itemname") itemname: String,
        @Query("itemid") itemid: String,
        @Query("amount") amount: String,
        @Query("trayid") trayid: String,
        @Query("COLNUMBER") colnumber: String
    ): Response<List<UpdateInvoiceResponse>>
}

object CreateInvoiceService {
    fun makeInvoiceService(): ICreateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_SECURE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ICreateInvoiceService::class.java)
    }
}