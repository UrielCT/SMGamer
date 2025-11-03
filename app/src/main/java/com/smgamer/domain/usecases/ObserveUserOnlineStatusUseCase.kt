package com.smgamer.domain.usecases

import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject

class ObserveUserOnlineStatusUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String, onStatusChange: (Boolean, Long) -> Unit) =
        repository.observeUserOnlineStatus(userId, onStatusChange)
}