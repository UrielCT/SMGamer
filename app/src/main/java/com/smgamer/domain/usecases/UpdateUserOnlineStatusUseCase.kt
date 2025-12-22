package com.smgamer.domain.usecases

import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.datastore.remote.UserService
import javax.inject.Inject

class UpdateUserOnlineStatusUseCase @Inject constructor(
    private val firestoreService: UserService
) {
    suspend operator fun invoke(userId: String, isOnline: Boolean) {
        firestoreService.updateUserOnlineStatus(userId, isOnline)
    }
}