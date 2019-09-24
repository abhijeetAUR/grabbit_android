package com.example.grabbit.bnhome.bnhome

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface IHomeService {
    @POST(UrlEndpoints.productListHome)
    suspend fun getProductListService(@Query("Kioskid") Kioskid: String): Response<HomeResponse>
}

object HomeFactory {
    fun makeHomeService(): IHomeService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IHomeService::class.java)
    }
}



