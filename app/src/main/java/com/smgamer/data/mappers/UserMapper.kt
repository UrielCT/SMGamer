package com.smgamer.data.mappers

import com.google.firebase.auth.FirebaseUser
import com.smgamer.data.model.CommentDto
import com.smgamer.data.model.LikeDto
import com.smgamer.data.model.PostDto
import com.smgamer.data.model.UserDto
import com.smgamer.domain.model.Comment
import com.smgamer.domain.model.Like
import com.smgamer.domain.model.Post
import com.smgamer.domain.model.User
import java.util.Date

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    password = password,
    username = username,
    phone = phone,
    profileImage = profileImage,
    coverImage = coverImage,
    timestamp = timestamp
)

fun User.toDto() = UserDto(
    id = id,
    email = email,
    password = password,
    username = username,
    phone = phone,
    profileImage = profileImage,
    coverImage = coverImage,
    timestamp = timestamp
)

fun FirebaseUser.toDomain(): User {
    return User(
        id = uid,
        email = email ?: "",
        username = displayName ?: "",
        phone = phoneNumber ?: "",
        profileImage = photoUrl?.toString() ?: "",
        timestamp = Date().time
    )
}




fun PostDto.toDomain() = Post(
    id = id,
    idUser = idUser,
    title = title,
    description = description,
    images = images,
    category = category,
    timestamp = timestamp
)

fun Post.toDto() = PostDto(
    id = id,
    idUser = idUser,
    title = title,
    description = description,
    images = images,
    category = category,
    timestamp = timestamp
)



fun LikeDto.toDomain() = Like(
    id = id,
    idPost = idPost,
    idUser = idUser,
    timestamp = timestamp
)

fun Like.toDto() = LikeDto(
    id = id,
    idPost = idPost,
    idUser = idUser,
    timestamp = timestamp
)


fun CommentDto.toDomain() = Comment(
    id = id,
    comment = comment,
    idPost = idPost,
    idUser = idUser,
    timestamp = timestamp
)

fun Comment.toDto() = CommentDto(
    id = id,
    comment = comment,
    idPost = idPost,
    idUser = idUser,
    timestamp = timestamp
)

