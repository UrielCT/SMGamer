package com.example.socialmediagamer.model.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.socialmediagamer.R
import com.example.socialmediagamer.base.BaseViewHolder
import com.example.socialmediagamer.model.models.Message
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.utils.RelativeTime
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class MessageAdapter(val context: Context, options: FirestoreRecyclerOptions<Message>) : FirestoreRecyclerAdapter<Message, BaseViewHolder<*>>(options) {

    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mAuthProvider: AuthProvider



    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, message: Message) {
        when(holder){
            is MessageViewholder -> holder.bind(message, position)
            else -> IllegalArgumentException("se olvido de pasar el viewholder en el bind")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MessageViewholder(
            LayoutInflater.from(context).inflate(R.layout.cardview_message, parent, false)
        )
    }




    inner class MessageViewholder(itemView:View) : BaseViewHolder<Message>(itemView) {
        val textViewMessage: TextView = itemView.findViewById(R.id.txt_message)
        val textViewDate: TextView = itemView.findViewById(R.id.txt_date_message)
        val imageViewViewed: ImageView = itemView.findViewById(R.id.im_viewed_message)
        val linearLayoutMessage : LinearLayout = itemView.findViewById(R.id.ll_message)


        override fun bind(item: Message, position: Int) {
            mUsersProvider = UsersProvider()
            mAuthProvider = AuthProvider()
            val document: DocumentSnapshot = snapshots.getSnapshot(position)
            val messageId: String = document.id

            textViewMessage.setText(item.message)
            val relativeMessage:String = RelativeTime.timeFormatAMPM(item.timestamp,context).toString()
            textViewDate.text = relativeMessage

            if(item.idSender.equals(mAuthProvider.getUid())){
                val params :RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.setMargins(150,0,0,0)
                linearLayoutMessage.layoutParams = params
                linearLayoutMessage.setPadding(30,20,0,20)
                linearLayoutMessage.setBackgroundResource(R.drawable.rounded_linear_layout)
                linearLayoutMessage.visibility = View.VISIBLE
                textViewMessage.setTextColor(Color.WHITE)
                textViewDate.setTextColor(Color.LTGRAY)
            }
            else{
                val params :RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                params.setMargins(0,0,150,0)
                linearLayoutMessage.layoutParams = params
                linearLayoutMessage.setPadding(30,20,30,20)
                linearLayoutMessage.setBackgroundResource(R.drawable.rounded_linear_layout_grey)
                imageViewViewed.visibility = View.GONE
                textViewMessage.setTextColor(Color.DKGRAY)
                textViewDate.setTextColor(Color.LTGRAY)
            }

            if (item.viewed){
                imageViewViewed.setImageResource(R.drawable.icon_check_blue)
            }
            else{
                imageViewViewed.setImageResource(R.drawable.icon_check_grey)

            }



            itemView.setOnClickListener {
                //goToChatActivity(chatId,item.idUser1,item.idUser2)
            }
        }


    }

}