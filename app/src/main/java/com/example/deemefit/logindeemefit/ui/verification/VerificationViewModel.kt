package com.example.deemefit.logindeemefit.ui.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deemefit.logindeemefit.core.Event
import com.example.deemefit.logindeemefit.domain.SendEmailVerificationUseCase
import com.example.deemefit.logindeemefit.domain.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    val verifyEmailUseCase: VerifyEmailUseCase
): ViewModel() {

    private val _navigateToVerifyAccount = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyAccount: LiveData<Event<Boolean>>
        get() = _navigateToVerifyAccount

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    init {
        viewModelScope.launch { sendEmailVerificationUseCase() }
        viewModelScope.launch {

            verifyEmailUseCase()
                .catch {
                    Timber.i("Verification error: ${it.message}")
                }
                .collect{ verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }
        }
    }

    fun onGoToDetailSelected() {
        _navigateToVerifyAccount.value = Event(true)
    }
}