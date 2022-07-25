package com.example.socialmediagamer.model.models

data class Chat(
    var id:String="",
    var idUser1:String = "",
    var idUser2:String="",
    var idNotification: Int = 0,
    var isWriting:Boolean=false,
    var timestamp:Long = 0,
    var ids:ArrayList<String> = arrayListOf()
)
