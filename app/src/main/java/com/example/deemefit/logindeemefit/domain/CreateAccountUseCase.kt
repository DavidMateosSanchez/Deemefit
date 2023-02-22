package com.example.deemefit.logindeemefit.domain

import com.example.deemefit.logindeemefit.data.network.AuthenticationService
import com.example.deemefit.logindeemefit.data.network.UserService
import com.example.deemefit.logindeemefit.ui.signin.model.UserSignIn
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
){

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        val accountCreated = authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
        return if (accountCreated) {
            userService.createUserTable(userSignIn)
        } else {
            false
        }
    }
}