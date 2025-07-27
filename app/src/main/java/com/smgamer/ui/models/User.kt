package com.smgamer.ui.models

import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val username: String = "",
    val timestamp: Long = 0,
    val profileImage: String = "" // Añade este campo para la foto
)