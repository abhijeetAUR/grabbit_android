package com.example.grabbit.signup

import com.example.grabbit.login.SignupResponse
import com.example.grabbit.network_layer.BASE_URL
import com.example.grabbit.network_layer.UrlEndpoints
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ISignupService {
    @FormUrlEncoded
    @POST(UrlEndpoints.newRegistration)
    suspend fun getSignupResponse(
        @Field("UserName") UserName: String,
        @Field("FullName") FullName: String,
        @Field("ContactNumber") ContactNumber: String,
        @Field("EmailAddress") EmailAddress: String,
        @Field("Password") Password: String
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


