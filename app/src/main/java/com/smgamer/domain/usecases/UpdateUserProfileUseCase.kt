package com.smgamer.domain.usecases

import android.content.Context
import android.net.Uri
import com.smgamer.domain.model.User
import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.usecases.cloudinary.DeleteImageFromCloudinaryUseCase
import com.smgamer.domain.usecases.cloudinary.UploadImageToCloudinaryUseCase
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadImageToCloudinaryUseCase: UploadImageToCloudinaryUseCase,
    private val deleteImageFromCloudinaryUseCase: DeleteImageFromCloudinaryUseCase
) {
    suspend operator fun invoke(
        context: Context,
        currentUser: User,
        name: String,
        email: String,
        phone: String,
        profileUri: Uri?,
        coverUri: Uri?
    ): User {
        var newProfileUrl = currentUser.profileImage
        var newCoverUrl = currentUser.coverImage

        // COVER
        if (coverUri != null) {
            val uploadedCover = uploadImageToCloudinaryUseCase(context, coverUri, "smgamer/coverImages")
            if (!uploadedCover.isNullOrEmpty()) {
                if (currentUser.coverImage.isNotEmpty()) {
                    deleteImageFromCloudinaryUseCase(currentUser.coverImage)
                }
                newCoverUrl = uploadedCover
            }
        }

        // PROFILE
        if (profileUri != null) {
            val uploadedProfile = uploadImageToCloudinaryUseCase(context, profileUri, "smgamer/userImages")
            if (!uploadedProfile.isNullOrEmpty()) {
                if (currentUser.profileImage.isNotEmpty()) {
                    deleteImageFromCloudinaryUseCase(currentUser.profileImage)
                }
                newProfileUrl = uploadedProfile
            }
        }

        val updatedUser = currentUser.copy(
            username = name,
            email = email,
            phone = phone,
            profileImage = newProfileUrl,
            coverImage = newCoverUrl
        )

        userRepository.updateUser(updatedUser)
        return updatedUser
    }
}