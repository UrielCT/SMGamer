package com.example.socialmediagamer.model.models

data class FCMBody(
     var to:String,
     var priority:String,
     var ttl:String,
     var data:Map<String,String>

)
