package com.smgamer.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.smgamer.data.datastore.local.UserLocalDataSource
import com.smgamer.data.datastore.remote.FirebaseAuthService
import com.smgamer.data.datastore.remote.FirestoreService
import com.smgamer.data.datastore.remote.UserService
import com.smgamer.data.mappers.toDomain
import com.smgamer.data.mappers.toDto
import com.smgamer.domain.repository.UserRepository
import com.smgamer.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val localDataSource: UserLocalDataSource,
    private val userService: UserService
): UserRepository {

    override suspend fun signInWithGoogle(credential: AuthCredential): Result<User> {
        return try {
            val userDto = authService.signInWithGoogle(credential)
            userService.createUserIfNotExists(userDto)

            // Actualizar estado online
            userService.updateUserOnlineStatus(userDto.id, true)

            userService.fixUsersOnlineField()

            val fullUser = userService.getUserById(userDto.id) ?: userDto
            val user = fullUser.toDomain().copy(isOnline = true)

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val userDto = authService.signInWithEmail(email, password)

            // Actualizar estado online
            userService.updateUserOnlineStatus(userDto.id, true)

            userService.fixUsersOnlineField()

            val fullUser = userService.getUserById(userDto.id) ?: userDto
            val user = fullUser.toDomain().copy(isOnline = true)

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        username: String,
        phone: String
    ): Result<User> {
        return try {
            val userDto = authService.signUpWithEmail(email, password, username, phone)
            userService.createUserIfNotExists(userDto)

            userService.updateUserOnlineStatus(userDto.id, true)

            userService.fixUsersOnlineField()

            val user = userDto.toDomain().copy(isOnline = true)

            //guardar user localmente
            localDataSource.saveUser(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(
        email: String,
        password: String,
        username: String,
        phone: String
    ): Result<User> {
        return try {
            val userDto = authService.createUser(email, password, username, phone)
            userService.saveUser(userDto)

            userService.updateUserOnlineStatus(userDto.id, true)

            userService.fixUsersOnlineField()

            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val localUser = localDataSource.getUser()
        if (localUser != null) return localUser

        val firebaseUser = authService.getCurrentUser() ?: return null
        val firestoreUser = userService.getUserById(firebaseUser.uid)

        val user = firestoreUser?.toDomain() ?: firebaseUser.toDomain()
        localDataSource.saveUser(user)

        return user
    }


    override suspend fun getUserById(userId: String): User? {
        return try {
            userService.getUserById(userId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override fun getUserByIdFlow(userId: String): Flow<User?> {
        return userService.getUserByIdFlow(userId).map { it?.toDomain() }
    }

//    fun getUserByIdFlow(userId: String): Flow<UserDto?> = callbackFlow {
//        val listener = usersCollection.document(userId).addSnapshotListener { snapshot, error ->
//            if (error != null) { close(error); return@addSnapshotListener }
//            val dto = snapshot?.toObject(UserDto::class.java)?.copy(id = snapshot.id)
//            trySend(dto).isSuccess
//        }
//        awaitClose { listener.remove() }
//    }

    override suspend fun logout() {
        val localUser = localDataSource.getUser()
        localUser?.let {
            userService.updateUserOnlineStatus(it.id, false)
        }
        authService.logout()
        localDataSource.clearUser()
    }

    override suspend fun updateUser(user: User) {
        userService.updateUser(user.toDto())
        localDataSource.saveUser(user)
    }


    override fun setupRealtimeConnectionTracking() {
        userService.setupRealtimeConnectionTracking()
    }

    override fun setUserOnlineStatus(isOnline: Boolean) {
        userService.setUserOnlineStatus(isOnline)
    }

    override fun observeUserOnlineStatus(
        userId: String,
        onStatusChange: (Boolean, Long) -> Unit
    ) {
        userService.observeUserOnlineStatus(userId, onStatusChange)
    }


//    private val database = FirebaseDatabase.getInstance().reference
//
//    override fun setUserOnlineStatus(isOnline: Boolean) {
//        val userId = authService.getCurrentUser()?.uid ?: return
//        val userStatusRef = database.child("users_status").child(userId)
//
//        // Actualizamos el estado
//        val status = mapOf(
//            "online" to isOnline,
//            "lastSeen" to ServerValue.TIMESTAMP
//        )
//        userStatusRef.updateChildren(status)
//    }
//
//    override fun setupRealtimeStatusTracking() {
//        val userId = authService.getCurrentUser()?.uid ?: return
//        val userStatusRef = database.child("users_status").child(userId)
//
//        val connectedRef = database.child(".info/connected")
//
//        connectedRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (connected) {
//                    // Cuando el usuario se conecta
//                    userStatusRef.onDisconnect().updateChildren(
//                        mapOf(
//                            "online" to false,
//                            "lastSeen" to ServerValue.TIMESTAMP
//                        )
//                    )
//                    userStatusRef.updateChildren(
//                        mapOf(
//                            "online" to true,
//                            "lastSeen" to ServerValue.TIMESTAMP
//                        )
//                    )
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }


}