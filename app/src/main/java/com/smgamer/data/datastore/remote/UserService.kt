package com.smgamer.data.datastore.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.smgamer.data.model.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuthService,
    private val realtimeDatabase: FirebaseDatabase
) {
    private val usersCollection = firestore.collection("Users")

    // Corrige usuarios que tengan el campo viejo "online" y lo reemplaza por "isOnline"
    suspend fun fixUsersOnlineField() {
        try {
            val snapshot = usersCollection.get().await()
            for (doc in snapshot.documents) {
                val data = doc.data ?: continue
                val hasOldField = data.containsKey("online")
                val hasNewField = data.containsKey("isOnline")

                if (hasOldField && !hasNewField) {
                    val onlineValue = data["online"] as? Boolean ?: false
                    doc.reference.update(
                        mapOf(
                            "isOnline" to onlineValue,
                            "online" to com.google.firebase.firestore.FieldValue.delete()
                        )
                    ).await()
                } else if (hasOldField && hasNewField) {
                    // Limpia el campo viejo si aún existe
                    doc.reference.update("online", com.google.firebase.firestore.FieldValue.delete()).await()
                }
            }
            println(" Campos 'online' reemplazados por 'isOnline' correctamente.")
        } catch (e: Exception) {
            println(" Error al corregir campos duplicados: ${e.message}")
        }
    }


    // Guardar o actualiza usuario
    suspend fun saveUser(user: UserDto) {
        usersCollection.document(user.id).set(user).await()
    }

    // Crear usuario solo si no existe
    suspend fun createUserIfNotExists(user: UserDto) {
        val doc = usersCollection.document(user.id).get().await()
        if (!doc.exists()) {
            usersCollection.document(user.id).set(user).await()
        }
    }

    // cambiar a Flow
    // traer usuario por ID
    suspend fun getUserById(userId: String): UserDto? {
        return try {
            usersCollection.document(userId).get().await().toObject(UserDto::class.java)
        } catch (e: Exception) {
            null
        }
    }


    fun getUserByIdFlow(userId: String): Flow<UserDto?> = callbackFlow {
        val listener = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(UserDto::class.java)?.copy(id = snapshot.id)
                trySend(user).isSuccess
            }

        awaitClose { listener.remove() }
    }





    // actualizar usuario
    suspend fun updateUser(userDto: UserDto) {
        usersCollection.document(userDto.id).set(userDto).await()
    }

    suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean) {
        val updateData = if (isOnline) {
            mapOf("isOnline" to true)
        } else {
            mapOf(
                "isOnline" to false,
                "lastConnection" to System.currentTimeMillis()
            )
        }
        usersCollection.document(userId).update(updateData).await()
    }


    private val userStatusRef: DatabaseReference
        get() = realtimeDatabase.reference
            .child("users_status")
            .child(auth.getCurrentUser()?.uid ?: "")

    fun setupRealtimeConnectionTracking() {
        val connectedRef = realtimeDatabase.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    userStatusRef.onDisconnect().updateChildren(
                        mapOf(
                            "online" to false,
                            "lastSeen" to ServerValue.TIMESTAMP
                        )
                    )
                    userStatusRef.updateChildren(
                        mapOf(
                            "online" to true,
                            "lastSeen" to ServerValue.TIMESTAMP
                        )
                    )
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun setUserOnlineStatus(isOnline: Boolean) {
        val status = mapOf(
            "online" to isOnline,
            "lastSeen" to ServerValue.TIMESTAMP
        )
        userStatusRef.updateChildren(status)
    }

    fun observeUserOnlineStatus(
        userId: String,
        onStatusChange: (Boolean, Long) -> Unit
    ): ValueEventListener {
        val ref = realtimeDatabase.reference.child("users_status").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val online = snapshot.child("online").getValue(Boolean::class.java) ?: false
                val lastSeen = snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
                onStatusChange(online, lastSeen)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun removeListener(userId: String, listener: ValueEventListener) {
        realtimeDatabase.reference.child("users_status").child(userId)
            .removeEventListener(listener)
    }

}