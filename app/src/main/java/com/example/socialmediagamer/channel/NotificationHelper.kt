package com.example.socialmediagamer.channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.example.socialmediagamer.R
import com.example.socialmediagamer.model.models.Message
import java.util.*

class NotificationHelper(context: Context) : ContextWrapper(context){
    private val CHANNEL_ID = "com.example.socialmediagamer"
    private val CHANNEL_NAME = "SocialMediaGamer"

    private var manager : NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels(){
        val notificationChannel:NotificationChannel= NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.GRAY
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager().createNotificationChannel(notificationChannel)
    }

    fun getManager():NotificationManager{
        if (manager == null){
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    fun getNotification(title:String,body:String):NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setColor(Color.GRAY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }

    fun getNotificationMessage(
        messages:Array<Message>,
        usernameSender:String,
        usernameReceiver:String,
        lastMessage:String,
        bitmapSender:Bitmap,
        bitmapReceiver:Bitmap,
        action:NotificationCompat.Action
        ):NotificationCompat.Builder{


        var person1: Person? = null

        if (bitmapReceiver == null){
            person1 = Person.Builder()
            .setName(usernameReceiver)
                .setIcon(IconCompat.createWithResource(applicationContext,R.drawable.ic_person_confirm))
                .build()
        }
        else{
            person1= Person.Builder()
            .setName(usernameReceiver)
                .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                .build()
        }


        var person2:Person? = null

        if (bitmapSender == null){
            person2 = Person.Builder()
            .setName(usernameSender)
                .setIcon(IconCompat.createWithResource(applicationContext,R.drawable.ic_person_confirm))
                .build()
        }
        else{
            person2 = Person.Builder()
                .setName(usernameSender)
                .setIcon(IconCompat.createWithBitmap(bitmapSender))
                .build()
        }



        val messagingStyle :NotificationCompat.MessagingStyle = NotificationCompat.MessagingStyle(person1)
        val message1:NotificationCompat.MessagingStyle.Message =
            NotificationCompat.MessagingStyle.Message(
                lastMessage,
                Date().time,
                person1)
        messagingStyle.addMessage(message1)

        for(m in messages){
            val message2:NotificationCompat.MessagingStyle.Message =
                NotificationCompat.MessagingStyle.Message(
                    m.message,
                    m.timestamp,
                    person2)
            messagingStyle.addMessage(message2)
        }
        return NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(messagingStyle)
            .addAction(action)
    }




    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels()
        }
    }
}