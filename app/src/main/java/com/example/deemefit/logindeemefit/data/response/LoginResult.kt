package com.example.deemefit.logindeemefit.data.response

sealed class LoginResult {
    object Error : LoginResult()
    data class Success(val verified:Boolean) : LoginResult()
}