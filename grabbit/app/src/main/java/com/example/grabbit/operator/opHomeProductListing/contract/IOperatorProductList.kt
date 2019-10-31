package com.example.grabbit.operator.opHomeProductListing.contract

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints.Companion.productlistForLoading
import com.example.grabbit.operator.opHomeProductListing.model.OPProductListResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IOperatorProductList {
    @FormUrlEncoded
    @POST(productlistForLoading)
    suspend fun getOperatorProductList(
        @Field("MOBILENO") mobileNo: String,
        @Field("PASSWORD") PASSWORD: String
    ): Response<OPProductListResponse>
}

object OperatorProductListFactory {
    fun makeOperatorProductList(): IOperatorProductList {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build().create(IOperatorProductList::class.java)
    }
}