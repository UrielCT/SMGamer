package com.smgamer.data.model

data class UserDto(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val coverImage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

