package com.example.grabbit.operator.opProductDetailPage.contract

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import com.example.grabbit.operator.opProductDetailPage.model.ProductLoadResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IProductLoadFactory {
    @FormUrlEncoded
    @POST(UrlEndpoints.operatorProductListLoadByOperator)
    suspend fun loadProductService(@Field("MOBILENO") mobileNo: String,
//                                   @Field("PASSWORD") password: String,
                                   @Field("kioskid") kioskId: String,
                                   @Field("COLNUMBER") colNumber: String,
                                   @Field("TRAYID") trayId: String,
                                   @Field("ITEMID") itemId: String,
                                   @Field("QTY") quantity: String): Response<List<ProductLoadResponse>>
}

object ProductLoadFactory {
    fun makeLoadService(): IProductLoadFactory {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build().create(IProductLoadFactory::class.java)
    }
}