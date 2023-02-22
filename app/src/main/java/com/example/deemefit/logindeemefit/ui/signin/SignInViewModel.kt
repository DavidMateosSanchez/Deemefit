package com.example.deemefit.logindeemefit.ui.signin

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deemefit.logindeemefit.core.Event
import com.example.deemefit.logindeemefit.domain.CreateAccountUseCase
import com.example.deemefit.logindeemefit.ui.signin.model.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val createAccountUseCase: CreateAccountUseCase) : ViewModel() {

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

    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    private var _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean>
        get() = _showErrorDialog

    fun onSignInSelected(userSignIn: UserSignIn) {
        val viewState = userSignIn.toSignInViewState()
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signInUser(userSignIn)
        } else {
            onFieldsChanged(userSignIn)
        }
    }

    private fun signInUser(userSignIn: UserSignIn) {
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            val accountCreated = createAccountUseCase(userSignIn)
            if (accountCreated) {
                _navigateToVerifyEmail.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(userSignIn: UserSignIn) {
        _viewState.value = userSignIn.toSignInViewState()
    }

    private fun isValidOrEmptyEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String): Boolean = passwordRegex.matcher(password).matches() || password.isEmpty()

    private fun isValidAndSamePasswords(password: String, passwordConfirmation: String): Boolean =  password==passwordConfirmation || passwordConfirmation.isEmpty()

    private fun UserSignIn.toSignInViewState(): SignInViewState {
        return SignInViewState(
            isValidEmail = isValidOrEmptyEmail(email),
            isValidPassword = isValidOrEmptyPassword(password),
            isPasswordsAreSame = isValidAndSamePasswords(password, passwordConfirmation)
        )
    }


}