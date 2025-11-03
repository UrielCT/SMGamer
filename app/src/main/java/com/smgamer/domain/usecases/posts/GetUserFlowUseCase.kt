package com.smgamer.domain.usecases.posts

import com.smgamer.domain.repository.UserRepository
import javax.inject.Inject

class GetUserFlowUseCase  @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(userId: String) = userRepository.getUserByIdFlow(userId)
}