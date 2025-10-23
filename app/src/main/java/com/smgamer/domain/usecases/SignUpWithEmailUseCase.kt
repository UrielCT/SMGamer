package com.smgamer.domain.usecases

import com.smgamer.domain.model.User
import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        phone: String
    ): Result<User> {
        return userRepository.signUpWithEmail(
            email,
            password,
            username,
            phone
        )
    }
}