package com.example.grabbit.login

import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface ILoginService {
        @POST(UrlEndpoints.userDetails)
        suspend fun getLoginResponse(@Query("mobileNo") mobileNo: String,
                                     @Query("password") password: String): Response<List<LoginResponse>>
}

object LoginFactory {
    fun makeLoginService(): ILoginService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ILoginService::class.java)
    }
}


