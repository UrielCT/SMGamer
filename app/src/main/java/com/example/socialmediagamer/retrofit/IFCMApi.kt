package com.example.socialmediagamer.retrofit

import com.example.socialmediagamer.model.models.FCMBody
import com.example.socialmediagamer.model.models.FCMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMApi {


    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAANVVasn0:APA91bHVfilwZP2nIkTtE3qIPvEsPEErCpE-6KMbNDIAJieMheGfZWyewX2ttMdqtd7p_Ps-xbNZfHrIJ30d7aVjQxw2jvJrODpXHgou-wbi0JkrWaFKsmHtrCbzWepdW2yRSws9A6UC\t\n"
    )
    @POST("fcm/send")
    fun send(@Body body:FCMBody):Call<FCMResponse>
}