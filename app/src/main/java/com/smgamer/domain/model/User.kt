package com.smgamer.domain.model


data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val coverImage: String = "",
    val isOnline: Boolean = false,
    val lastConnection:Long = 0,
    val timestamp: Long = 0
)