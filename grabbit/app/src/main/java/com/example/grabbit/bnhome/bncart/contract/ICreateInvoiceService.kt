package com.example.grabbit.bnhome.bncart.contract
import com.example.grabbit.bnhome.bncart.model.CreateInvoiceResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ICreateInvoiceService {
    @FormUrlEncoded
    @POST(UrlEndpoints.createInvoice)
//    suspend fun getCreateInvoice(
//        @Field("mobileno") mobileno: String,
//        @Field("Kioskid") kioskid: String,
//        @Field("itemname") itemname: String,
//        @Field("itemid") itemid: String,
//        @Field("amount") amount: String,
//        @Field("trayid") trayid: String,
//        @Field("COLNUMBER") colnumber: String
//    ): Response<List<CreateInvoiceResponse>>
    suspend fun getCreateInvoice(
        @Field("mobileno") mobileno: String,
        @Field("Kioskid") kioskid: String,
        @Field("itemid") itemid: String
    ): Response<List<CreateInvoiceResponse>>
}

object CreateInvoiceService {
    fun makeInvoiceService(): ICreateInvoiceService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ICreateInvoiceService::class.java)
    }
}