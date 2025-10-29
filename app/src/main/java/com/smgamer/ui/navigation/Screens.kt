package com.smgamer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.smgamer.R
import com.smgamer.utils.Constants


enum class Destination(
    val route: String,
    val labelRes: Int? = null,
    val icon: ImageVector? = null,
    val contentDescription: String? = null
){
    HOME(route = Constants.NAV_HOME, labelRes = R.string.home_title,
        icon = Icons.Default.Home, contentDescription = "home view"),

    FILTERS(route = Constants.NAV_FILTERS, labelRes = R.string.filters_title,
        icon = Icons.Default.FilterList, contentDescription = "filters view"),

    CHATS(route = Constants.NAV_CHATS, labelRes = R.string.chats_title,
        icon = Icons.AutoMirrored.Filled.Chat, contentDescription = "chats view"),

    PROFILE(route = Constants.NAV_PROFILE, labelRes = R.string.profile_title,
        icon = Icons.Default.Person, contentDescription = "profile view"),

    LOGIN(route = Constants.NAV_LOGIN),
    REGISTER(route = Constants.NAV_REGISTER),
    EDIT_PROFILE(route = Constants.NAV_EDIT_PROFILE),
    FILTERED_POSTS(route = Constants.NAV_FILTERED_POSTS),
    POST_DETAIL(route = "${Constants.NAV_POST_DETAIL}/{postId}"),
    USER_PROFILE(route = Constants.NAV_USER_PROFILE),
    NEW_POST(route = Constants.NAV_NEW_POST),
    CHAT_DETAIL(route = Constants.NAV_CHAT_DETAIL),
}


//sealed class Destination(
//    val route: String,
//    val label: String? = null,
//    val icon: ImageVector? = null,
//    val contentDescription: String? = null
//) {
//    object Home : Destination("home", "Home", Icons.Default.Home, "home view")
//    object Filters : Destination("filters", "Filters", Icons.Default.FilterList, "filters view")
//    object Chats : Destination("chats", "Chats", Icons.AutoMirrored.Filled.Chat, "chats view")
//    object Profile : Destination("profile", "Profile", Icons.Default.Person, "profile view")
//
//    // Pantallas sin barra
//    object Login : Destination("login")
//    object Register : Destination("register")
//    object EditProfile : Destination("edit_profile")
//    object FilteredPosts : Destination("filtered_posts")
//    object PostDetail : Destination("post_detail")
//    object UserProfile : Destination("user_profile")
//    object NewPost : Destination("new_post")
//    object ChatDetail : Destination("chat_detail")
//
//
//    companion object {
//        val bottomBarItems = listOf(Home, Filters, Chats, Profile)
//        fun fromRoute(route: String?): Destination? = when (route) {
//            Home.route -> Home
//            Filters.route -> Filters
//            Chats.route -> Chats
//            Profile.route -> Profile
//            EditProfile.route -> EditProfile
//            FilteredPosts.route -> FilteredPosts
//            PostDetail.route -> PostDetail
//            NewPost.route -> NewPost
//            UserProfile.route -> UserProfile
//            Login.route -> Login
//            Register.route -> Register
//            ChatDetail.route -> ChatDetail
//            else -> null
//        }
//    }
//}