package com.example.socialmediagamer.view.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediagamer.R
import com.example.socialmediagamer.model.adapters.CommentAdapter
import com.example.socialmediagamer.model.adapters.SliderAdapter
import com.example.socialmediagamer.databinding.ActivityPostDetailBinding
import com.example.socialmediagamer.model.models.Comment
import com.example.socialmediagamer.model.models.FCMBody
import com.example.socialmediagamer.model.models.FCMResponse
import com.example.socialmediagamer.model.models.SliderItem
import com.example.socialmediagamer.model.providers.*
import com.example.socialmediagamer.utils.RelativeTime
import com.example.socialmediagamer.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    private lateinit var mSliderAdapter: SliderAdapter
    private var mSliderItems:MutableList<SliderItem> = ArrayList()
    private lateinit var mExtraPostId :String

    private lateinit var mPostProvider: PostProvider
    private lateinit var mUsersProvider: UsersProvider
    private lateinit var mCommentsProvider: CommentsProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mLikesProvider: LikesProvider
    private lateinit var mNotificationProvider: NotificationProvider
    private lateinit var mTokenProvider: TokenProvider

    private lateinit var mCommentsAdapter: CommentAdapter

    private lateinit var mListener : ListenerRegistration

    private var mIdUser:String = ""
    private var mTimestamp : Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPostProvider = PostProvider()
        mUsersProvider = UsersProvider()
        mCommentsProvider = CommentsProvider()
        mAuthProvider = AuthProvider()
        mLikesProvider = LikesProvider()
        mNotificationProvider = NotificationProvider()
        mTokenProvider = TokenProvider()


        //setSupportActionBar(binding.toolbar)
        //supportActionBar!!.setTitle("")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.recyclerViewComments.layoutManager = LinearLayoutManager(this)

        mExtraPostId = intent.getStringExtra("id").toString()



        binding.fabComment.setOnClickListener { showDialogComment() }



        binding.btnShowProfile.setOnClickListener { goToShowProfile() }


        getPost()
        getNumberLikes()

    }
    //(value?.size() ?: error) as Int
    private fun getNumberLikes() {
        mListener = mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener { value, error ->
            if (value != null){
                val numberLikes: String = value?.size().toString()
                if (numberLikes == "1"){
                    binding.txtLikes.text = "$numberLikes Me gusta"
                }
                else{
                    binding.txtLikes.text = "$numberLikes Me gustas"
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val query: Query = mCommentsProvider.getCommentsByPost(mExtraPostId)
        val opciones : FirestoreRecyclerOptions<Comment> = FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java).build()
        mCommentsAdapter = CommentAdapter(this, opciones)
        binding.recyclerViewComments.adapter = mCommentsAdapter
        mCommentsAdapter.startListening()
        ViewedMessageHelper().updateOnline(true,this)
    }

    override fun onStop() {
        super.onStop()
        mCommentsAdapter.stopListening()
    }



    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null){
            mListener.remove()
        }
    }


    private fun showDialogComment(){
        var alert:AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setTitle("comentario")
        alert.setMessage("Ingresa tu comentario")

        val editText= EditText(this)
        editText.setHint("texto")

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(36,0,36,36)
        editText.layoutParams = params
        val container:RelativeLayout = RelativeLayout(this)
        val relativeParams:RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        container.layoutParams= relativeParams
        container.addView(editText)
        alert.setView(container)


        alert.setPositiveButton("OK",DialogInterface.OnClickListener { dialogInterface, i ->
            val value = editText.text.toString()
            if (value.isNotEmpty()){
                createComment(value)
            }
            else{
                Toast.makeText(this,"Debe ingresar el comentario",Toast.LENGTH_SHORT).show()
            }

        })

        alert.setNegativeButton("Cancelar",DialogInterface.OnClickListener { dialogInterface, i ->

        })
        alert.show()
    }




    private fun createComment(value:String){
        val comment:Comment = Comment()
        comment.comment = value
        comment.idPost = mExtraPostId
        comment.idUser = mAuthProvider.getUid().toString()
        comment.timestamp = Date().time
        mCommentsProvider.create(comment).addOnCompleteListener {
            if (it.isSuccessful){
                sendNotification(value)
                Toast.makeText(this,"El comentario se creo correctamente",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"no se pudo crear el comentario",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotification(comment: String){
        if (mIdUser == null){
            return
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("token")){
                    val token:String = it.getString("token").toString()
                    val data: Map<String,String> = hashMapOf(
                        "title" to "NUEVO COMENTARIO",
                        "body" to comment
                    )
                    val body: FCMBody = FCMBody(token,"high","4500s", data)
                    mNotificationProvider.sendNotification(body).enqueue(object : Callback<FCMResponse> {
                        override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                            Toast.makeText(this@PostDetailActivity,"la wea falló",Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                            if (response.body() != null){
                                if (response.body()!!.success == 1){
                                    Toast.makeText(this@PostDetailActivity,"La notificacion se envio correctamente",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this@PostDetailActivity,"La notificacion no se pudo enviar",Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                Toast.makeText(this@PostDetailActivity,"La notificacion no se pudo enviar",Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }
            }
            else{
                Toast.makeText(this,"El token del usuario no exixte",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun goToShowProfile(){
        if (mIdUser.isNotEmpty()){
            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra("idUser",mIdUser)
            startActivity(intent)
        }
        else{
            Toast.makeText(this,"el id del usuario aun no se carga",Toast.LENGTH_SHORT).show()
        }
    }




    private fun instanceSlider(){
        mSliderAdapter = SliderAdapter(this,mSliderItems)
        binding.imageSlider.setSliderAdapter(mSliderAdapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        binding.imageSlider.indicatorSelectedColor = Color.WHITE
        binding.imageSlider.indicatorUnselectedColor = Color.GRAY
        binding.imageSlider.scrollTimeInSec = 3
        binding.imageSlider.isAutoCycle = true
        binding.imageSlider.startAutoCycle()
    }



    private fun getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("image1")){
                    val image1 :String = it.getString("image1").toString()
                    val item:SliderItem = SliderItem()
                    item.imageUrl = image1
                    mSliderItems.add(item)
                }
                if (it.contains("image2")){
                    val image2 :String = it.getString("image2").toString()
                    val item : SliderItem = SliderItem()
                    item.imageUrl = image2
                    mSliderItems.add(item)
                }
                if (it.contains("title")){
                    val title:String = it.getString("title").toString()
                    binding.txtTitulo.text = title.uppercase()
                }
                if (it.contains("descripcion")){
                    val description:String = it.getString("descripcion").toString()
                    binding.txtDescription.text = description
                }
                if (it.contains("category")){
                    val category:String = it.getString("category").toString()
                    binding.txtCategory.text = category

                    if (category == "PS4"){
                        binding.imCategory.setImageResource(R.drawable.icon_ps4)
                    }
                    else if (category == "XBOX"){
                        binding.imCategory.setImageResource(R.drawable.icon_xbox)
                    }
                    else if (category == "PC"){
                        binding.imCategory.setImageResource(R.drawable.icon_pc)
                    }
                    else if (category == "NINTENDO"){
                        binding.imCategory.setImageResource(R.drawable.icon_nintendo)
                    }
                }
                if (it.contains("idUser")){
                    mIdUser = it.getString("idUser").toString()
                    getUserInfo(mIdUser)
                }
                if (it.contains("timestamp")){
                    mTimestamp = it.getLong("timestamp") ?: 0

                    val relativeTime = RelativeTime.getTimeAgo(mTimestamp,this)
                    binding.txtRelativeTime.text = relativeTime
                }
                instanceSlider()
            }
        }
    }

    private fun getUserInfo(idUser: String) {
        mUsersProvider.getUser(idUser).addOnSuccessListener {
            if (it.exists()){
                if (it.contains("username")){
                    val username:String = it.getString("username").toString()
                    binding.txtUsername.text = username
                }
                if (it.contains("phone")){
                    val phone:String = it.getString("phone").toString()
                    binding.txtPhone.text = phone
                }
                if (it.contains("image_profile")){
                    val imageProfile:String = it.getString("image_profile").toString()

                    if (!imageProfile.isNullOrEmpty()){
                        Picasso.get().load(imageProfile).fit().into(binding.imCircleProfile)
                    }

                }
            }
        }
    }

    
}




