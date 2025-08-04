package com.smgamer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Destination(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null,
    val contentDescription: String? = null
) {
    object Home : Destination("home", "Home", Icons.Default.Home, "home view")
    object Filters : Destination("filters", "Filters", Icons.Default.FilterList, "filters view")
    object Chats : Destination("chats", "Chats", Icons.AutoMirrored.Filled.Chat, "chats view")
    object Profile : Destination("profile", "Profile", Icons.Default.Person, "profile view")

    // Pantallas sin barra
    object Login : Destination("login")
    object Register : Destination("register")
    object EditProfile : Destination("edit_profile")
    object FilteredPosts : Destination("filtered_posts")
    object PostDetail : Destination("post_detail")
    object UserProfile : Destination("user_profile")
    object NewPost : Destination("new_post")


    companion object {
        val bottomBarItems = listOf(Home, Filters, Chats, Profile)
        fun fromRoute(route: String?): Destination? = when (route) {
            Home.route -> Home
            Filters.route -> Filters
            Chats.route -> Chats
            Profile.route -> Profile
            EditProfile.route -> EditProfile
            FilteredPosts.route -> FilteredPosts
            PostDetail.route -> PostDetail
            NewPost.route -> NewPost
            UserProfile.route -> UserProfile
            Login.route -> Login
            Register.route -> Register
            else -> null
        }
    }
}