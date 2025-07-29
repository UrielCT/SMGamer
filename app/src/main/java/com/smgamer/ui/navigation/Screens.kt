package com.smgamer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

enum class Destination(
    val route:String,
    val label:String,
    val icon:ImageVector,
    val contentDescription:String
){
    HOME(route = "nav_home", label = "Home", icon = Icons.Default.Home, contentDescription = "home view"),
    FILTERS(route = "nav_filters", label = "Filters", icon = Icons.Default.FilterList, contentDescription = "filters view"),
    CHATS(route = "nav_chats", label = "Chats", icon = Icons.AutoMirrored.Default.Chat, contentDescription = "chats view"),
    PROFILE(route = "nav_profile", label = "Profile", icon = Icons.Default.Person, contentDescription = "profile view"),
}


//sealed class BottomBarScreen(
//    val route: String,
//    val title: String,
//    val icon: ImageVector
//) {
//    object Home : BottomBarScreen(
//        route = "home",
//        title = "Home",
//        icon = Icons.Default.Home
//    )
//
//    object Filters : BottomBarScreen(
//        route = "filters",
//        title = "Filters",
//        icon = Icons.Default.FilterList
//    )
//
//    object Chats : BottomBarScreen(
//        route = "Chats",
//        title = "Chats",
//        icon = Icons.AutoMirrored.Filled.Chat
//    )
//    object Profile : BottomBarScreen(
//        route = "profile",
//        title = "Profile",
//        icon = Icons.AutoMirrored.Filled.Chat
//    )
//}


@Serializable
object Login

@Serializable
object Register