package com.example.grabbit.paytm

import com.example.grabbit.login.LoginResponse
import com.example.grabbit.network_layer.UrlEndpoints
import com.example.grabbit.network_layer.UrlEndpoints.Companion.paytm
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

val api = "?=123456&=12132&=12&=test@gmail.com"

interface IPaytmService {
    @POST(UrlEndpoints.paytm)
    suspend fun getLoginResponse(@Query("order_id") orderId: String,
                                 @Query("customer_id") password: String,
                                 @Query("amount") amount: String,
                                 @Query("email") email: String): Response<List<LoginResponse>>
}

object PaytmFactory {
    fun makePaytmService(): IPaytmService {
        return Retrofit.Builder()
            .baseUrl("http://localhost:3000/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IPaytmService::class.java)
    }
}