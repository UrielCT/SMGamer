package com.smgamer.di

import com.smgamer.data.datastore.local.UserLocalDataSource
import com.smgamer.data.datastore.remote.ChatService
import com.smgamer.data.datastore.remote.CommentService
import com.smgamer.data.datastore.remote.FirebaseAuthService
import com.smgamer.data.datastore.remote.LikeService
import com.smgamer.data.datastore.remote.MessageService
import com.smgamer.data.datastore.remote.PostService
import com.smgamer.data.datastore.remote.UserService
import com.smgamer.data.datastore.remote.cloudinary.CloudinaryService
import com.smgamer.data.repository.ChatRepositoryImpl
import com.smgamer.data.repository.CloudinaryRepositoryImpl
import com.smgamer.data.repository.CommentRepositoryImpl
import com.smgamer.data.repository.LikeRepositoryImpl
import com.smgamer.data.repository.MessageRepositoryImpl
import com.smgamer.data.repository.PostRepositoryImpl
import com.smgamer.data.repository.UserRepositoryImpl
import com.smgamer.domain.repository.ChatRepository
import com.smgamer.domain.repository.CloudinaryRepository
import com.smgamer.domain.repository.CommentRepository
import com.smgamer.domain.repository.LikeRepository
import com.smgamer.domain.repository.MessageRepository
import com.smgamer.domain.repository.PostRepository
import com.smgamer.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        authService: FirebaseAuthService,
        userLocalDataSource: UserLocalDataSource,
        userService: UserService
    ): UserRepository = UserRepositoryImpl(
        authService,
        localDataSource = userLocalDataSource,
        userService = userService,
    )


    @Provides
    @Singleton
    fun providePostRepository(
        postService: PostService,
        userService: UserService,
        likeService: LikeService,
        commentService: CommentService
    ): PostRepository = PostRepositoryImpl(
        postService = postService,
        userService = userService,
        likeService = likeService,
        commentService = commentService
    )

    @Provides
    @Singleton
    fun provideLikeRepository(
        likeService: LikeService
    ): LikeRepository = LikeRepositoryImpl(
        likeService = likeService
    )

    @Provides
    @Singleton
    fun provideCommentRepository(
        commentService: CommentService,
        userService: UserService
    ): CommentRepository = CommentRepositoryImpl(
        commentService = commentService,
        userService = userService
    )

    @Provides
    @Singleton
    fun provideChatRepository(
        chatService: ChatService
    ): ChatRepository = ChatRepositoryImpl(
        chatService = chatService
    )

    @Provides
    @Singleton
    fun provideMessageRepository(
        messageService: MessageService
    ): MessageRepository = MessageRepositoryImpl(
        messageService = messageService
    )



    @Provides
    @Singleton
    fun provideCloudinaryRepository(
        cloudinaryService: CloudinaryService
    ): CloudinaryRepository = CloudinaryRepositoryImpl(cloudinaryService)


}
