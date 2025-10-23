package com.smgamer.data.mappers

import com.google.firebase.auth.FirebaseUser
import com.smgamer.data.model.UserDto
import com.smgamer.domain.model.User
import java.util.Date

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    password = password,
    username = username,
    phone = phone,
    profileImage = profileImage,
    coverImage = coverImage,
    timestamp = timestamp
)

fun User.toDto() = UserDto(
    id = id,
    email = email,
    password = password,
    username = username,
    phone = phone,
    profileImage = profileImage,
    coverImage = coverImage,
    timestamp = timestamp
)

fun FirebaseUser.toDomain(): User {
    return User(
        id = uid,
        email = email ?: "",
        username = displayName ?: "",
        phone = phoneNumber ?: "",
        profileImage = photoUrl?.toString() ?: "",
        timestamp = Date().time
    )
}