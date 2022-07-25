package com.example.socialmediagamer.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.socialmediagamer.R
import com.example.socialmediagamer.channel.NotificationHelper
import com.example.socialmediagamer.model.models.Message
import com.example.socialmediagamer.receivers.MessageReceiver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.random.Random

class MyFirebaseMessagingClient :FirebaseMessagingService() {

    val NOTIFICATION_REPLY = "NotificationReply"


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data:Map<String,String> = message.data
        val title:String = data["title"].toString()
        val body:String = data["body"].toString()

        if (title !=null){
            if (title.equals("NUEVO MENSAJE")){
                showNotificationMessage(data)
            }
            else{
                showNotification(title, body)

            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }


    private fun showNotification(title: String,body:String){
        val notificationHelper:NotificationHelper= NotificationHelper(baseContext)
        val builder:NotificationCompat.Builder = notificationHelper.getNotification(title,body)
        val n = Random.nextInt(1000000)
        notificationHelper.getManager().notify(n,builder.build())

    }

    private fun showNotificationMessage(data:Map<String,String>){

        val imageSender:String = data.get("imageSender").toString()
        val imageReceiver:String = data.get("imageReceiver").toString()
        Log.d("ENTRO","NUEVO MENSAJE")
        getImageSender(data,imageSender, imageReceiver)

    }



    private fun getImageSender(data:Map<String,String>,imageSender:String,imageReceiver: String){
        Handler(Looper.getMainLooper())
            .post(Runnable {
                kotlin.run {
                    Picasso.get().load(imageSender).fit().into(object : Target{
                        override fun onBitmapLoaded(bitmapSender: Bitmap, from: Picasso.LoadedFrom?) {
                            getImageReceiver(data,imageReceiver,bitmapSender)

                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            getImageReceiver(data,imageReceiver,null)
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            TODO("Not yet implemented")
                        }

                    })

                }
            })

    }

    private fun getImageReceiver(data:Map<String,String>, imageReceiver:String, bitmapSender: Bitmap?){
        Picasso.get().load(imageReceiver).into(object : Target{
            override fun onBitmapLoaded(
                bitmapReceiver: Bitmap,
                from: Picasso.LoadedFrom?) {

                notifyMessage(data,bitmapSender!!, bitmapReceiver)

            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                notifyMessage(data, bitmapSender!!,null)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun notifyMessage(data: Map<String, String>,bitmapSender :Bitmap, bitmapReceiver:Bitmap?){

        val usernameSender:String = data.get("usernameSender").toString()
        val usernameReceiver:String = data.get("usernameReceiver").toString()
        val lastMessage:String = data.get("lastMessage").toString()
        val messagesJson:String = data.get("messages").toString()
        val imageSender:String = data.get("imageSender").toString()
        val imageReceiver:String = data.get("imageReceiver").toString()

        val idSender:String = data.get("idSender").toString()
        val idReceiver:String = data.get("idReceiver").toString()
        val idChat:String = data.get("idChat").toString()
        val idNotification:Int = data.get("idNotification")!!.toInt()

        val intent: Intent =Intent(this,MessageReceiver::class.java)
        intent.putExtra("idSender",idSender)
        intent.putExtra("idReceiver",idReceiver)
        intent.putExtra("idChat",idChat)
        intent.putExtra("idNotification",idNotification)
        intent.putExtra("usernameSender",usernameSender)
        intent.putExtra("usernameReceiver",usernameReceiver)
        intent.putExtra("imageReceiver",imageReceiver)
        intent.putExtra("imageSender",imageSender)


        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteInput:RemoteInput = RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje ...").build()

        val action:NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "Responder",
            pendingIntent)
            .addRemoteInput(remoteInput)
            .build()


        val gson:Gson= Gson()
        val messages = gson.fromJson(messagesJson, Array<Message>::class.java)

        val notificationHelper:NotificationHelper= NotificationHelper(baseContext)
        val builder:NotificationCompat.Builder =
            notificationHelper.getNotificationMessage(
                messages,
                usernameSender,
                usernameReceiver,
                lastMessage,
                bitmapSender!!,
                bitmapReceiver!!,
                action
            )
        notificationHelper.getManager().notify(idNotification,builder.build())
    }
}