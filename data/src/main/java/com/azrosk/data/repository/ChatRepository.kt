package com.azrosk.data.repository

import android.util.Log
import com.azrosk.data.model.MessageItem
import com.azrosk.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: DatabaseReference,
) {

    suspend fun getUsers(): List<Users> {
        return try {
            val usersCollection = firestore.collection("users")
            val querySnapshot = usersCollection.get().await()
            val usersList = mutableListOf<Users>()
            for (document in querySnapshot) {
                val user = document.toObject(Users::class.java)
                if (user.role != "admin") {
                    usersList.add(user)
                }
            }
            usersList
        } catch (e: Exception) {
            // Handle any errors or exceptions here
            Log.d("getUsers", e.message.toString())
            emptyList()
        }
    }

    suspend fun sendMessage(receiverUid: String, senderUid: String, msgObject: MessageItem) {
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid

        database.child("chats").child("senderRoom").child(senderRoom).child("messages").push()
            .setValue(msgObject).addOnSuccessListener {
                database.child("chats").child("receiverRoom").child(receiverRoom).child("messages")
                    .push().setValue(msgObject)
            }.await()
    }

    suspend fun displayMsg(senderRoom: String): List<MessageItem> {
        return suspendCancellableCoroutine { continuation ->
            val messageList = mutableListOf<MessageItem>()

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val msg = postSnapshot.getValue(MessageItem::class.java)
                        msg?.let { messageList.add(it) }
                    }
                    continuation.resume(messageList.toList()) // Resume with fetched list
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException()) // Resume with error
                }
            }

            // Add ValueEventListener to fetch data
            val databaseRef =
                database.child("chats").child("senderRoom").child(senderRoom).child("messages")
            databaseRef.addListenerForSingleValueEvent(eventListener)

            // Remove the listener when coroutine is cancelled
            continuation.invokeOnCancellation {
                databaseRef.removeEventListener(eventListener)
            }
        }
    }

}