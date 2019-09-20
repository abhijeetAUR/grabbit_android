package com.example.grabbit.login

data class LoginResponse(val Result: String,
                         val Balance: String,
                         val KioskID: String,
                         val UserID: String,
                         val Fullname: String,
                         val Username: String,
                         val userTypeID: String,
                         val ClientID: String
)