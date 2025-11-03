package com.smgamer.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.smgamer.domain.usecases.SignUpWithEmailUseCase
import com.smgamer.domain.usecases.SignUpWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val signUpWithGoogleUseCase: SignUpWithGoogleUseCase
): ViewModel(){
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state = _state.asStateFlow()

    fun registerWithEmail(
        email: String,
        password: String,
        username: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            val result = signUpWithEmailUseCase(email, password, username, phone)
            result.onSuccess {
                _state.value = RegisterState.Success(it)
                onSuccess()
            }.onFailure {
                _state.value = RegisterState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun registerWithGoogle(
        credential: AuthCredential,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            val result = signUpWithGoogleUseCase(credential)
            result.onSuccess {
                _state.value = RegisterState.Success(it)
                onSuccess()
            }.onFailure {
                _state.value = RegisterState.Error(it.message ?: "Error desconocido")
            }
        }
    }
}