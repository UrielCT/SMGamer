package com.example.socialmediagamer.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.socialmediagamer.R
import com.example.socialmediagamer.databinding.ActivityHomeBinding
import com.example.socialmediagamer.view.fragments.ChatsFragment
import com.example.socialmediagamer.view.fragments.FiltersFragment
import com.example.socialmediagamer.view.fragments.HomeFragment
import com.example.socialmediagamer.view.fragments.ProfileFragment
import com.example.socialmediagamer.model.providers.AuthProvider
import com.example.socialmediagamer.model.providers.TokenProvider
import com.example.socialmediagamer.model.providers.UsersProvider
import com.example.socialmediagamer.utils.ViewedMessageHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var mTokenProvider:TokenProvider
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mUsersProvider: UsersProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mTokenProvider = TokenProvider()
        mAuthProvider = AuthProvider()
        mUsersProvider = UsersProvider()
        openFragment(HomeFragment())

        createToken()


        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.itemHome -> {
                    openFragment(HomeFragment())
                    true
                }
                R.id.itemPerfil -> {
                    openFragment(ProfileFragment())
                    true
                }
                R.id.itemChats -> {
                    openFragment(ChatsFragment())
                    true
                }
                R.id.itemFiltros -> {
                    openFragment(FiltersFragment())
                    true
                }
                else -> false
            }

        }


    }


    override fun onStart() {
        super.onStart()
        ViewedMessageHelper().updateOnline(true,this)

    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper().updateOnline(false,this)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // navegacion entre fragments
    private fun openFragment(fragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun createToken(){
        mTokenProvider.create(mAuthProvider.getUid().toString())
    }
}