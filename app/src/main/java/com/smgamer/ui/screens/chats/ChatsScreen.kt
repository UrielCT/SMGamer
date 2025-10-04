package com.smgamer.ui.screens.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smgamer.R
import com.smgamer.ui.components.ChatsCardItem
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.DividerThickness

data class Contact(
    val id:Int,
    val name: String,
    val lastMessage: String,
    val unreadCount: Int,
    val profileImageRes: Int,
    val isOnline: Boolean = false
)

@Composable
fun ChatsScreen(
    modifier: Modifier = Modifier,
    navToChatDetail: () -> Unit
) {
    val contactsList: List<Contact> = listOf(
        Contact(0,"Juan", "Hola", 3, R.drawable.ic_person, isOnline = true),
        Contact(1,"Maria", "Hosdfasdg", 300, R.drawable.ic_person, isOnline = false),
        Contact(2,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(3,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(4,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(5,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(6,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(7,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(8,"Pablo", "Hogala", 0, R.drawable.ic_person, isOnline = true),
        Contact(9,"Mati", "Hofla", 1000, R.drawable.ic_person, isOnline = true),
        Contact(10,"Tomi", "Hffffffa", 0, R.drawable.ic_person, isOnline = true),
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(CommonPaddingMin)
    ) {
        items(items = contactsList, key = { it.id } ) { chat ->
            ChatsCardItem(
                name = chat.name,
                lastMessage = chat.lastMessage,
                unreadCount = chat.unreadCount,
                profileImageRes = chat.profileImageRes,
                isOnline = chat.isOnline,
                navToChatDetail = { navToChatDetail() }
            )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = CommonPaddingDefault),
                    thickness = DividerThickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
        }
    }
}