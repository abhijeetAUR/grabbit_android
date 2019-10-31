package com.example.grabbit.operator.opLogin.contract


import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import com.example.grabbit.operator.opLogin.model.OperatorLoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface IOpLoginService {
    @FormUrlEncoded
    @POST(UrlEndpoints.operatorLogin)
    suspend fun postOperatorLoginDetails(@Field("MOBILENO") mobileNo: String,
                                         @Field("PASSWORD") password: String) : Response<List<OperatorLoginResponse>>
}

object OpLoginService {
    fun makeOpLoginService(): IOpLoginService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build().create(IOpLoginService::class.java)
    }
}