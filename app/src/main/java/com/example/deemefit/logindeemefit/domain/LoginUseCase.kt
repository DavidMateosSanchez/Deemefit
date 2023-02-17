package com.example.deemefit.logindeemefit.domain

import com.example.deemefit.logindeemefit.data.network.AuthenticationService
import com.example.deemefit.logindeemefit.data.response.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke(email: String, password: String): LoginResult =
        authenticationService.login(email, password)

}