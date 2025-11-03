package com.smgamer.domain.usecases

import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject

class SetUserOnlineStatusUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(isOnline: Boolean) = repository.setUserOnlineStatus(isOnline)
}