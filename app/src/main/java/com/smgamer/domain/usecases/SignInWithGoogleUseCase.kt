package com.smgamer.domain.usecases

import com.google.firebase.auth.AuthCredential
import com.smgamer.domain.model.User
import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject


class SignInWithGoogleUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(credential: AuthCredential): Result<User> {
        return userRepository.signInWithGoogle(credential)
    }
}