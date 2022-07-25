package com.example.socialmediagamer.model.models

data class Post(
    var id:String="",
    var title:String="",
    var descripcion:String="",
    var image1:String="",
    var image2:String="",
    var idUser:String="",
    var category:String="",
    var timestamp:Long = 0

)
