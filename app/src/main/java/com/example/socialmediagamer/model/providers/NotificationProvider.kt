package com.example.socialmediagamer.model.providers

import com.example.socialmediagamer.model.models.FCMBody
import com.example.socialmediagamer.model.models.FCMResponse
import com.example.socialmediagamer.retrofit.IFCMApi
import com.example.socialmediagamer.retrofit.RetrofitClient
import retrofit2.Call

class NotificationProvider {

    private val url:String="https://fcm.googleapis.com"

    fun sendNotification(body:FCMBody):Call<FCMResponse>{
        return RetrofitClient().getClient(url).create(IFCMApi::class.java).send(body)
    }

}