package com.example.grabbit.scanner.Service

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import com.example.grabbit.scanner.Model.TDetailResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ITransactionDetailsService {
    @FormUrlEncoded
    @POST(UrlEndpoints.transactionDetails )
    suspend fun getTransactionDetails(@Field("mobileno") mobileNo : String): Response<TDetailResponse>
}

object TransactionDetailsService  {
    fun makeTransactionDetailsService(): ITransactionDetailsService  {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ITransactionDetailsService ::class.java)
    }
}