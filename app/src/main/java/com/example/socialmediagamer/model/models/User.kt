package com.example.socialmediagamer.model.models


// PROPIEDADES DEL USUARIO

data class User(
    var id:String="",
    var email:String="",
    var username:String="",
    var password:String="",
    var phone:String="",
    var timestamp:Long = 0,
    var image_profile:String="",
    var image_cover:String = "",
    val lastConnection:Long = 0,
    val online:Boolean? = null
    )
