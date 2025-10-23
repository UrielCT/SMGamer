package com.smgamer.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.smgamer.domain.usecases.SignInWithEmailUseCase
import com.smgamer.domain.usecases.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase
): ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    // iniciar sesion con google
    fun signInWithGoogleCredential(credential: AuthCredential, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = signInWithGoogleUseCase(credential)
            result.onSuccess {
                _state.value = LoginState.Success(it)
                onSuccess()
            }.onFailure {
                _state.value = LoginState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    // iniciar sesion con email y contraseña
    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = signInWithEmailUseCase(email, password)
            result.onSuccess {
                _state.value = LoginState.Success(it)
                onSuccess()
            }.onFailure {
                _state.value = LoginState.Error(it.message ?: "Error desconocido")
            }
        }
    }
}