package com.example.grabbit.bnhome.bnhome.contract

import com.example.grabbit.bnhome.bnhome.model.HomeResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IHomeService {
    @FormUrlEncoded
    @POST(UrlEndpoints.productListHome)
    suspend fun getProductListService(@Field("Kioskid") Kioskid: String): Response<HomeResponse>
}

object HomeFactory {
    fun makeHomeService(): IHomeService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IHomeService::class.java)
    }
}



