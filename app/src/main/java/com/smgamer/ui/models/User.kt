package com.smgamer.ui.models


data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val username: String = "",
    val timestamp: Long = 0,
    val profileImage: String = ""
)