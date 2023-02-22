package com.example.deemefit.logindeemefit.ui.signin

data class SignInViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidPassword: Boolean = true,
    val isPasswordsAreSame: Boolean = true
) {
    fun userValidated() = isValidEmail && isValidPassword && isPasswordsAreSame
}
