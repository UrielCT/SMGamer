package com.smgamer.ui.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.smgamer.R
import com.smgamer.ui.components.ChatsCardItem
import com.smgamer.ui.theme.SMGamerTheme
import com.smgamer.ui.theme.Yellow100

data class Contact(
    val name: String,
    val lastMessage: String,
    val unreadCount: Int,
    val profileImageRes: Int
)


@Composable
fun ChatsScreen(modifier: Modifier){
    Column (modifier = modifier
        .fillMaxSize()
        .background(Yellow100),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        val contactsList:List<Contact> = listOf(
            Contact(name = "Juan", lastMessage = "Hola", unreadCount = 3, profileImageRes = R.drawable.ic_person),
            Contact(name = "Maria", lastMessage = "Hosdfasdg", unreadCount = 300, profileImageRes = R.drawable.ic_person),
            Contact(name = "Pablo", lastMessage = "Hogala", unreadCount = 0, profileImageRes = R.drawable.ic_person),
            Contact(name = "Mati", lastMessage = "Hofla", unreadCount = 1000, profileImageRes = R.drawable.ic_person),
            Contact(name = "Tomi", lastMessage = "Hffffffa", unreadCount = 0, profileImageRes = R.drawable.ic_person)
        )

        contactsList.forEach { filter ->
            ChatsCardItem(
                name = filter.name,
                lastMessage = filter.lastMessage,
                unreadCount = filter.unreadCount,
                profileImageRes = filter.profileImageRes,
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatsScreenPreview(){
    SMGamerTheme {
        ChatsScreen(Modifier)
    }

}