package com.example.socialmediagamer.model.models

data class FCMResponse(
     var multicast_id:Long,
     var success:Int,
     var failure:Int,
    var canonical_ids:Int,
    var results:ArrayList<Object> = ArrayList<Object>()
)
