package com.example.socialmediagamer.model.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.socialmediagamer.R
import com.example.socialmediagamer.base.BaseViewHolder
import com.example.socialmediagamer.model.models.Chat
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.ChatsProvider
import com.example.socialmediagamer.model.providers.MessageProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.view.activities.ChatActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso

class ChatsAdapter(val context: Context, options: FirestoreRecyclerOptions<Chat>) : FirestoreRecyclerAdapter<Chat, BaseViewHolder<*>>(options) {

    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mCharsProvider: ChatsProvider
    private lateinit var mMessageProvider: MessageProvider

    private lateinit var mListener : ListenerRegistration
    private lateinit var mListenerLastMessage: ListenerRegistration



    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, chat: Chat) {
        when(holder){
            is ChatsAdapter.ChatViewholder -> holder.bind(chat, position)
            else -> IllegalArgumentException("se olvido de pasar el viewholder en el bind")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ChatViewholder(LayoutInflater.from(context).inflate(R.layout.cardview_chats, parent,false))
    }




    inner class ChatViewholder(itemView:View) : BaseViewHolder<Chat>(itemView){



        override fun bind(item: Chat, position: Int) {
            mUsersProvider = UsersProvider()
            mAuthProvider = AuthProvider()
            mCharsProvider = ChatsProvider()
            mMessageProvider = MessageProvider()



            val document: DocumentSnapshot = snapshots.getSnapshot(position)
            val chatId:String = document.id
            if (mAuthProvider.getUid().equals(item.idUser1)){
                getUserInfo(item.idUser2, itemView)
            }
            else{
                getUserInfo(item.idUser1, itemView)
            }


            itemView.setOnClickListener {
                goToChatActivity(chatId,item.idUser1,item.idUser2)
            }

            getLastMessage(chatId,itemView)

            var idSender = ""
            if (mAuthProvider.getUid().equals(item.idUser1)){
                idSender = item.idUser2
            }
            else{
                idSender = item.idUser1

            }
            getMessageNotRead(chatId,idSender, itemView)
        }


    }

    private fun getMessageNotRead(chatId: String, idSender: String, itemView: View) {
        mListener = mMessageProvider.getMessageByChatAndSender(chatId,idSender).addSnapshotListener { value, error ->
            if (value != null){
                val size:Int = value!!.size()
                if (size > 0){
                    itemView.findViewById<FrameLayout>(R.id.framelayoutMessageNotRead).visibility = View.VISIBLE
                    itemView.findViewById<TextView>(R.id.txt_message_not_readed).text = size.toString()
                }
                else{
                    itemView.findViewById<FrameLayout>(R.id.framelayoutMessageNotRead).visibility = View.GONE

                }
            }


        }
    }


    fun getListener():ListenerRegistration {
        return mListener
    }

    fun getListenerLastMessage():ListenerRegistration {
        return mListenerLastMessage
    }

    private fun getLastMessage(chatId: String, itemView: View) {
        mListenerLastMessage = mMessageProvider.getLastMessage(chatId).addSnapshotListener { value, error ->

            if (value != null){
                val size:Int = value!!.size()
                if (size > 0){
                    val lastMessage = value!!.documents.get(0).getString("message")
                    itemView.findViewById<TextView>(R.id.txt_last_message_chat).setText(lastMessage)
                }
            }

        }
    }


    private fun goToChatActivity(chatId:String,idUser1: String,idUser2: String){
        val intent: Intent= Intent(context,ChatActivity::class.java)
        intent.putExtra("idChat",chatId)
        intent.putExtra("idUser1",idUser1)
        intent.putExtra("idUser2",idUser2)
        context.startActivity(intent)
    }


    private fun getUserInfo(idUser:String,itemView: View){
        val chatUserImage : ImageView = itemView.findViewById(R.id.im_chat)
        val chatUsername : TextView = itemView.findViewById(R.id.txt_username_chat)
        val chatMessagesNotReaded : TextView = itemView.findViewById(R.id.txt_message_not_readed)
        //val chatLastMessage : TextView = itemView.findViewById(R.id.txt_last_message_chat)

        mUsersProvider.getUser(idUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("username")){
                    val username = it.getString("username")
                    chatUsername.text = username!!.uppercase()
                }
                if (it.contains("image_profile")){
                    val imageProfile = it.getString("image_profile")

                    println(imageProfile)
                    if (imageProfile != null){
                        if (imageProfile.isNotEmpty()){
                                Picasso.get().load(imageProfile).fit().into(chatUserImage)
                        }else{
                            chatUserImage.setImageResource(R.drawable.cover_image)
                        }
                    }


                }
            }
        }
    }


}