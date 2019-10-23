package com.example.grabbit.scanner

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints.Companion.balanceUser
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface IWalletService {
    @GET(balanceUser)
    suspend fun balanceWalletDetails(
        @Query("mobileno") mobileNo: String
    ): Response<List<GetWalletAmountResponse>>
}

object BalanceWalletDetailsService {
    fun makeInvoiceService(): IWalletService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(IWalletService::class.java)
    }
}

