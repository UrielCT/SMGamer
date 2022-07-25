package com.example.socialmediagamer.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediagamer.model.adapters.MessageAdapter
import com.example.socialmediagamer.databinding.ActivityChatBinding
import com.example.socialmediagamer.model.models.Chat
import com.example.socialmediagamer.model.models.FCMBody
import com.example.socialmediagamer.model.models.FCMResponse
import com.example.socialmediagamer.model.models.Message
import com.example.socialmediagamer.model.providers.*
import com.example.socialmediagamer.utils.RelativeTime
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var mExtraIdUser1:String
    private lateinit var mExtraIdUser2:String
    private lateinit var mExtraIdchat:String

    private var mIdNotificationChat:Long  = 0

    private lateinit var mChatsProvider: ChatsProvider
    private lateinit var mMessageProvider:MessageProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mAdapter : MessageAdapter
    private lateinit var mLinearLayoutManager:LinearLayoutManager
    private lateinit var mNotificationProvider: NotificationProvider
    private lateinit var mTokenProvider:TokenProvider

    private lateinit var mMyUsername:String
    private lateinit var mUsernameChat: String
    private var mImageReceiver:String = ""
    private var mImageSender:String = ""


    private lateinit var mListener : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.stackFromEnd = true
        binding.recyclerviewMessage.layoutManager = mLinearLayoutManager


        mExtraIdUser1 = intent.getStringExtra("idUser1").toString()
        mExtraIdUser2 = intent.getStringExtra("idUser2").toString()
        mExtraIdchat = intent.getStringExtra("idChat").toString()
        mChatsProvider = ChatsProvider()
        mMessageProvider = MessageProvider()
        mAuthProvider = AuthProvider()
        mUsersProvider = UsersProvider()
        mTokenProvider = TokenProvider()
        mNotificationProvider = NotificationProvider()

        getMyInfoUser()





        binding.btnSendMessage.setOnClickListener{
            sendMessage()
            checkIfChatExist()
        }

        binding.appbarChat.btnBack.setOnClickListener { finish() }

        getUserInfo()
        checkIfChatExist()
    }


    override fun onStart() {
        super.onStart()
        /*val query: Query = mMessageProvider.getMessageByChat(mExtraIdchat)
        val opciones : FirestoreRecyclerOptions<Message> = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
        mAdapter = MessageAdapter(this, opciones)*/
        getMessageChat()
        if(mAdapter != null){
            mAdapter.startListening()
        }
        ViewedMessageHelper().updateOnline(true,this)

    }

    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }


    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener!= null){
            mListener.remove()
        }
    }

    private fun getMessageChat(){
        val query: Query = mMessageProvider.getMessageByChat(mExtraIdchat)
        val opciones : FirestoreRecyclerOptions<Message> = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
        mAdapter = MessageAdapter(this, opciones)
        binding.recyclerviewMessage.adapter = mAdapter
        mAdapter.startListening()
        mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                updateViewed()
                val numberMessage:Int = mAdapter.itemCount
                val lastMessagePosition :Int = mLinearLayoutManager.findLastCompletelyVisibleItemPosition()

                if (lastMessagePosition == -1 || (positionStart >=(numberMessage - 1) && lastMessagePosition == (positionStart - 1))){
                    binding.recyclerviewMessage.scrollToPosition(positionStart)
                }
            }
        })
    }






    private fun sendMessage(){
        val textMessage:String = binding.etMessage.text.toString()


        if (textMessage.isNotEmpty()){
            val message:Message = Message()
            message.idChat = mExtraIdchat

            if (mAuthProvider.getUid().equals(mExtraIdUser1)){
                message.idSender= mExtraIdUser1
                message.idReceiver= mExtraIdUser2
            }
            else{
                message.idSender = mExtraIdUser2
                message.idReceiver = mExtraIdUser1
            }
            message.timestamp = Date().time
            message.viewed = false
            message.idChat = mExtraIdchat
            message.message = textMessage

            mMessageProvider.create(message).addOnCompleteListener {
                if (it.isSuccessful){
                    binding.etMessage.setText("")

                    mAdapter.notifyDataSetChanged()
                    getToken(message)

                } else{
                    Toast.makeText( this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show()
                }
            }.toString()

        }
    }


    private fun getUserInfo(){
        var idUserInfo:String=""
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserInfo = mExtraIdUser2
        }
        else{
            idUserInfo = mExtraIdUser1
        }
        mListener = mUsersProvider.getUserRealTime(idUserInfo).addSnapshotListener { it, error ->
            if (it != null) {
                if (it.exists()){
                    if (it.contains("username")){
                        mUsernameChat = it.getString("username").toString()
                        binding.appbarChat.txtUsername.text = mUsernameChat
                    }
                    if (it.contains("online")){
                        val online: Boolean = it.getBoolean("online")!!
                        if (online){
                            binding.appbarChat.txtEstado.text = "En linea"
                        }
                        else if (it.contains("lastConnect")){

                            val lastConnect:Long= it.getLong("lastConnect")!!.toLong()
                            val relativeTime:String = RelativeTime.getTimeAgo(lastConnect,this).toString()
                            binding.appbarChat.txtEstado.text = relativeTime

                        }
                    }

                    if (it.contains("image_profile")){
                        mImageReceiver = it.getString("image_profile").toString()
                        if (!mImageReceiver.isNullOrEmpty()){
                            Picasso.get().load(mImageReceiver).fit().into(binding.appbarChat.imUser)
                        }
                    }
                }
            }
        }
    }

    private fun checkIfChatExist(){
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser1,mExtraIdUser2).get().addOnSuccessListener {
            val size:Int = it.size()
            if (size == 0){
                createChat()
            }
            else{
                mExtraIdchat = it.documents.get(0).id
                mIdNotificationChat = it.documents.get(0).getLong("idNotification")!!
                getMessageChat()
                updateViewed()
            }
        }
    }

    private fun updateViewed(){
        var idSender = ""
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2
        }
        else{
            idSender = mExtraIdUser1
        }

        mMessageProvider.getMessageByChatAndSender(mExtraIdchat, idSender).get().addOnSuccessListener {
            for (i : DocumentSnapshot in it.documents){
                mMessageProvider.updateViewed(i.id ,true)
            }
        }
    }

    private fun createChat(){
        val chat:Chat=Chat()
        chat.idUser1 = mExtraIdUser1
        chat.idUser2 = mExtraIdUser2
        chat.isWriting = false
        chat.timestamp = Date().time
        chat.id = mExtraIdUser1 + mExtraIdUser2
        val n = Random.nextInt(1000000)
        chat.idNotification = n
        mIdNotificationChat = n.toLong()
        val ids:ArrayList<String> = arrayListOf()
        ids.add(mExtraIdUser1)
        ids.add(mExtraIdUser2)
        chat.ids = ids
        mChatsProvider.create(chat)
        mExtraIdchat= chat.id
        getMessageChat()
    }



    private fun getToken(message: Message){
        var idUser :String=""
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUser = mExtraIdUser2
        }
        else{
            idUser = mExtraIdUser1
        }
        mTokenProvider.getToken(idUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("token")){
                    val token:String = it.getString("token").toString()
                    getLastThreeMessages(message,token)
                }
            }
            else{
                Toast.makeText(this,"El token del usuario no exixte",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getLastThreeMessages(sms: Message,token:String){
        mMessageProvider.getLastThreeMessageByChatAndSender(mExtraIdchat, mAuthProvider.getUid().toString()).get().addOnSuccessListener {

            val messageArrayList:ArrayList<Message> = ArrayList()

            for (d in it.documents){
                if (d.exists()){
                    val message:Message = d.toObject(Message::class.java)!!
                    messageArrayList.add(message)
                }
            }

            if (messageArrayList.size == 0){
                messageArrayList.add(sms)
            }

            Collections.reverse(messageArrayList)

            val gson = Gson()
            val messages:String  = gson.toJson(messageArrayList)

            sendNotification(token,messages,sms)

        }
    }


    private fun sendNotification(token: String,messages:String,message: Message){
        val data: MutableMap<String,String> = hashMapOf(
            "title" to "NUEVO MENSAJE",
            "body" to message.message,
            "idNotification" to mIdNotificationChat.toString(),
            "messages" to messages,
            "usernameSender" to mMyUsername.uppercase(),
            "usernameReceiver" to mUsernameChat.uppercase(),

            "idSender" to message.idSender,
            "idReceiver" to message.idReceiver,
            "idChat" to message.idChat
        )

        if (mImageSender.equals("")){
            mImageSender ="IMAGEN_NO_VALIDA"
        }
        if (mImageReceiver.equals("")){
            mImageReceiver = "IMAGEN_NO_VALIDA"
        }

        data.put("imageSender", mImageSender.uppercase())
        data.put("imageReceiver",mImageReceiver.uppercase())

        var idSender:String = ""
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2
        }
        else{
            idSender = mExtraIdUser1
        }

        mMessageProvider.getLastMessageSender(mExtraIdchat, idSender).get().addOnSuccessListener {
            val size = it.size()
            var lastMessage:String = ""
            if (size > 0){
                lastMessage = it.documents.get(0).getString("message").toString()
                data.put("lastMessage", lastMessage)
            }
            val body: FCMBody = FCMBody(token,"high","4500s", data)
            mNotificationProvider.sendNotification(body).enqueue(object :
                Callback<FCMResponse> {
                override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                    Toast.makeText(this@ChatActivity,"la wea falló",Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                    if (response.body() != null){
                        if (response.body()!!.success == 1){
                            Toast.makeText(this@ChatActivity,"La notificacion se envio correctamente",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@ChatActivity,"La notificacion no se pudo enviar",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this@ChatActivity,"La notificacion no se pudo enviar",Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }


    }


    private fun getMyInfoUser(){
        mUsersProvider.getUser(mAuthProvider.getUid().toString()).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("username")){
                    mMyUsername = it.getString("username").toString()
                }
                if (it.contains("image_profile")){
                    mImageSender = it.getString("image_profile").toString()
                }
            }
        }
    }


}



