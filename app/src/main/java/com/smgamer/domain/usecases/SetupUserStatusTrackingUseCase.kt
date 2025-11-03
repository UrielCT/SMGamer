package com.smgamer.domain.usecases

import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject

class SetupUserStatusTrackingUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke() = repository.setupRealtimeConnectionTracking()
}