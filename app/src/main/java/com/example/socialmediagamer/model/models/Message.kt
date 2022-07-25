package com.example.socialmediagamer.model.models

data class Message (
        var id:String="",
        var idSender:String="",
        var idReceiver:String="",
        var idChat:String="",
        var message:String="",
        var timestamp:Long=0,
        var viewed:Boolean = false
        )