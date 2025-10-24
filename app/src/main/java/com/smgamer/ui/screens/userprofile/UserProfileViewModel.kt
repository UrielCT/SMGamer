package com.smgamer.ui.screens.userprofile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smgamer.domain.model.User
import com.smgamer.domain.usecases.GetCurrentUserUseCase
import com.smgamer.domain.usecases.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
) : ViewModel() {

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _isMyUser = mutableStateOf(false)
    val isMyUser: State<Boolean> = _isMyUser


    fun loadUserProfile(userId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            val localUser = getCurrentUserUseCase()
            if (localUser != null) {
                _user.value = localUser
            }

            try {
                val currentUser = getCurrentUserUseCase()
                if (userId == null || userId == currentUser?.id) {
                    _isMyUser.value = true
                    _user.value = currentUser
                } else {
                    _isMyUser.value = false
                    _user.value = getUserByIdUseCase(userId)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

}
