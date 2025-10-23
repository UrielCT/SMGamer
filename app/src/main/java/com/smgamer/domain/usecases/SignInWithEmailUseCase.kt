package com.smgamer.domain.usecases

import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.model.User
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return userRepository.signInWithEmail(email, password)
    }
}
