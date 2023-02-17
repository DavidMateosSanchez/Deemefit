package com.example.deemefit.logindeemefit.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deemefit.logindeemefit.core.Event
import com.example.deemefit.logindeemefit.data.response.LoginResult
import com.example.deemefit.logindeemefit.domain.LoginUseCase
import com.example.deemefit.logindeemefit.ui.login.model.UserLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginUseCase: LoginUseCase) : ViewModel() {

    // Con esto revisamos que la contraseña cumpla ciertas características para ser valida. MODIFICAR más tarde
    private companion object {
        val passwordRegex = Pattern.compile(
              "^" +
                    "(?=.*[0-9])" +         //Debe contener 1 número
                    "(?=.*[a-z])" +        //Debe contener 1 letra minúscula
                    "(?=.*[A-Z])" +        //Debe contener 1 letra mayúscula
                    "(?=\\S+$)" +           //No puede contener espacios en blanco
                    ".{4,}" +               //Debe contener mínimo 4 caracteres
                    "$"
        )
    }

    // Utilizaremos estas variables para definir cuando debemos navegar entre activities. Pendiente de crear Event en Core
    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _navigateToRecoverAccount = MutableLiveData<Event<Boolean>>()
    val navigateToRecoverAccount: LiveData<Event<Boolean>>
        get() = _navigateToRecoverAccount

    private val _navigateToSignIn = MutableLiveData<Event<Boolean>>()
    val navigateToSignIn: LiveData<Event<Boolean>>
        get() = _navigateToSignIn

    private val _navigateToVerifyAccount = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyAccount: LiveData<Event<Boolean>>
        get() = _navigateToVerifyAccount

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(UserLogin())
    val showErrorDialog: LiveData<UserLogin>
        get() = _showErrorDialog

    fun onLoginSelected(email: String, password: String) {
        if (isValidEmail(email) && isValidPassword(password)) {
            loginUser(email, password)
        } else {
            onFieldsChanged(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _viewState.value = LoginViewState(isLoading = true)
            when (val result = loginUseCase(email, password)) {
                LoginResult.Error -> {
                    _showErrorDialog.value =
                        UserLogin(email = email, password = password, showErrorDialog = true)
                    _viewState.value = LoginViewState(isLoading = false)
                }
                is LoginResult.Success -> {
                    if (result.verified) {
                        _navigateToHome.value = Event(true)
                    } else {
                        _navigateToVerifyAccount.value = Event(true)
                    }
                }
            }
            _viewState.value = LoginViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(email: String, password: String) {
        _viewState.value = LoginViewState(
            isValidEmail = isValidEmail(email),
            isValidPassword = isValidPassword(password)
        )
    }

    fun onForgotPasswordSelected() {
        _navigateToRecoverAccount.value = Event(true)
    }

    fun onSignInSelected() {
        _navigateToSignIn.value = Event(true)
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String): Boolean = passwordRegex.matcher(password).matches() || password.isEmpty()

}