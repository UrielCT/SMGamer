package com.example.socialmediagamer.receivers

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.socialmediagamer.model.models.FCMBody
import com.example.socialmediagamer.model.models.FCMResponse
import com.example.socialmediagamer.model.models.Message
import com.example.socialmediagamer.model.providers.MessageProvider
import com.example.socialmediagamer.model.providers.NotificationProvider
import com.example.socialmediagamer.model.providers.TokenProvider
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MessageReceiver : BroadcastReceiver() {

    val NOTIFICATION_REPLY = "NotificationReply"


    lateinit var mExtraIdSender:String
    lateinit var mExtraIdReceiver:String
    lateinit var mExtraIdChat:String
    lateinit var mExtraUsernameSender:String
    lateinit var mExtraUsernameReceiver:String
    lateinit var mExtraImageReceiver:String
    lateinit var mExtraImageSender:String

    lateinit var mTokenProvider: TokenProvider
    lateinit var mNotificationProvider : NotificationProvider

    var mExtraIdNotification:Int = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        mExtraIdSender = intent?.getStringExtra("idSender").toString()
        mExtraIdReceiver = intent?.getStringExtra("idReceiver").toString()
        mExtraIdChat = intent?.getStringExtra("idChat").toString()
        mExtraUsernameSender = intent?.getStringExtra("usernameSender").toString()
        mExtraUsernameReceiver = intent?.getStringExtra("usernameReceiver").toString()
        mExtraImageSender = intent?.getStringExtra("imageSender").toString()
        mExtraImageReceiver = intent?.getStringExtra("imageReceiver").toString()

        mExtraIdNotification = intent?.getIntExtra("idNotification",0)!!

        mTokenProvider = TokenProvider()
        mNotificationProvider =NotificationProvider()

        val notificationManager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(mExtraIdNotification)

        val message:String = getMessageText(intent).toString()

        sendMessage(message)
    }

    private fun sendMessage(messageText:String) {
        val message: Message = Message()
        message.idChat = mExtraIdChat
        message.idSender = mExtraIdReceiver
        message.idReceiver = mExtraIdSender
        message.timestamp = Date().time
        message.viewed = false
        message.idChat = mExtraIdChat
        message.message = messageText

        val messageProvider = MessageProvider()
        messageProvider.create(message).addOnCompleteListener {
            if (it.isSuccessful){

                getToken(message)

            }
        }.toString()
    }

    private fun getToken(message:Message){
        mTokenProvider.getToken(mExtraIdSender).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("token")){
                    val token:String = it.getString("token").toString()
                    val gson = Gson()
                    val messagesArray:ArrayList<Message> = ArrayList()
                    messagesArray.add(message)
                    val messages:String  = gson.toJson(messagesArray)
                    sendNotification(token,messages,message)
                }
            }
        }
    }


    private fun sendNotification(token: String,messages:String,message: Message){
        val data: MutableMap<String,String> = hashMapOf(
            "title" to "NUEVO MENSAJE",
            "body" to message.message,
            "idNotification" to mExtraIdNotification.toString(),
            "messages" to messages,
            "usernameSender" to mExtraUsernameReceiver.uppercase(),
            "usernameReceiver" to mExtraUsernameSender.uppercase(),

            "idSender" to message.idSender,
            "idReceiver" to message.idReceiver,
            "idChat" to message.idChat
        )
        data.put("imageSender", mExtraImageReceiver.uppercase())
        data.put("imageReceiver",mExtraImageSender.uppercase())


        val body: FCMBody = FCMBody(token,"high","4500s", data)
        mNotificationProvider.sendNotification(body).enqueue(object :
            Callback<FCMResponse> {
            override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                Log.d("ERROR","El error fue: " + t.message)
            }

            override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {

            }

        })


    }


    private fun getMessageText(intent: Intent?): CharSequence? {
        val remoteInput: Bundle = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null){
            return remoteInput.getCharSequence(NOTIFICATION_REPLY)
        }
        return null
    }

}