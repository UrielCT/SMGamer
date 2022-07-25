package com.example.socialmediagamer.model.models

data class Comment(
    var id: String= "",
    var comment: String="",
    var idUser: String="",
    var idPost: String="",
    var timestamp: Long = 0
)
