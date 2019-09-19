package com.example.grabbit.login

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST

interface ILoginService {
        @POST("/Userdetails.asmx/USER_LOGIN?mobileno=9890698284&password=grabbit123")
        suspend fun getLoginResponse(): Response<List<LoginResponse>>
}

object LoginFactory {
    const val BASE_URL = "http://grabbit.myvend.in"

    fun makeLoginService(): ILoginService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ILoginService::class.java)
    }
}

data class LoginResponse(val Result: String,
                         val Balance: String,
                         val KioskID: String,
                         val UserID: String,
                         val Fullname: String,
                         val Username: String,
                         val userTypeID: String,
                         val ClientID: String
                         )
