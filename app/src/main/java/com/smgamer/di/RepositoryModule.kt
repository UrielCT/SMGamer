package com.smgamer.di

import com.smgamer.data.datastore.local.UserLocalDataSource
import com.smgamer.data.datastore.remote.FirebaseAuthService
import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.datastore.remote.cloudinary.CloudinaryService
import com.smgamer.data.repository.CloudinaryRepositoryImpl
import com.smgamer.data.repository.PostRepositoryImpl
import com.smgamer.data.repository.UserRepositoryImpl
import com.smgamer.domain.repository.CloudinaryRepository
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
        firestoreService: FirestoreService,
        userLocalDataSource: UserLocalDataSource
    ): UserRepository = UserRepositoryImpl(authService, firestoreService, userLocalDataSource)


    @Provides
    @Singleton
    fun providePostRepository(
        firestoreService: FirestoreService,
    ): PostRepository = PostRepositoryImpl(firestoreService)


    @Provides
    @Singleton
    fun provideCloudinaryRepository(
        cloudinaryService: CloudinaryService
    ): CloudinaryRepository = CloudinaryRepositoryImpl(cloudinaryService)


}
