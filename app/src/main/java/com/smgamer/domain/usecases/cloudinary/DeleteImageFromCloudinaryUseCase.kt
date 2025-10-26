package com.smgamer.domain.usecases.cloudinary

import com.smgamer.domain.repository.CloudinaryRepository
import javax.inject.Inject

class DeleteImageFromCloudinaryUseCase @Inject constructor(
    private val cloudinaryRepository: CloudinaryRepository
) {
    suspend operator fun invoke(imageUrl: String) {
        cloudinaryRepository.deleteImage(imageUrl)
    }
}