package com.example.deemefit.logindeemefit.domain

import com.example.deemefit.logindeemefit.data.network.AuthenticationService
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}