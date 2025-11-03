package com.smgamer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.usecases.SetUserOnlineStatusUseCase
import com.smgamer.domain.usecases.SetupUserStatusTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    //private val userRepository: UserRepository
    private val setupUserStatusTracking: SetupUserStatusTrackingUseCase,
    private val setUserOnlineStatus: SetUserOnlineStatusUseCase
) : ViewModel() {

    init {
        setupUserStatusTracking()
    }

    fun setOnline() {
        viewModelScope.launch {
            setUserOnlineStatus(true)
        }
    }

    fun setOffline() {
        viewModelScope.launch {
            setUserOnlineStatus(false)
        }
    }
}