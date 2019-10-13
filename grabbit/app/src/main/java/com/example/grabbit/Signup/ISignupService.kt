package com.example.grabbit.Signup

import com.example.grabbit.login.SignupResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface ISignupService {
    @POST(UrlEndpoints.newRegistration)
    suspend fun getSignupResponse(
        @Query("UserName") UserName: String,
        @Query("FullName") FullName: String,
        @Query("ContactNumber") ContactNumber: String,
        @Query("EmailAddress") EmailAddress: String,
        @Query("Password") Password: String
    ): Response<List<SignupResponse>>
}

object SignupFactory {
    fun makeSignupService(): ISignupService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ISignupService::class.java)
    }
}


