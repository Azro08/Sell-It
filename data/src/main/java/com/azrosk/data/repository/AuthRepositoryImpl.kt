package com.azrosk.data.repository

import android.util.Log
import com.azrosk.data.api.UserService
import com.azrosk.data.model.Users
import com.azrosk.domain.model.User
import com.azrosk.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val api : UserService
) : AuthRepository {


    override suspend fun login(email: String, password: String): String {
        val deferred = CompletableDeferred<String>()
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deferred.complete("Done")
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        deferred.complete(errorMessage)
                    }
                }
        } catch (e: Exception) {
            deferred.complete(e.message ?: "Unknown error")
        }

        return deferred.await()
    }


    override suspend fun signup(email: String, password: String): String {
        val deferred = CompletableDeferred<String>()

        try {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deferred.complete(task.result.user?.uid ?: "")
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        deferred.complete(errorMessage)
                    }
                }
        } catch (e: Exception) {
            deferred.complete(e.message ?: "Unknown error")
        }

        return deferred.await()
    }

    override suspend fun saveUser(user: User): String {
        val usersCollection = firestore.collection("users")
        return try {
            // Firestore-specific logic here
            val userDocRef = usersCollection.document(user.id)
            userDocRef.set(user).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    override suspend fun deleteAccount(uid: String) = api.deleteUser(uid)

    override suspend fun getUserRole(): String? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            return try {
                val usersCollection = firestore.collection("users")
                val userDocument = usersCollection.document(uid)
                val documentSnapshot = userDocument.get().await()
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(Users::class.java)
                    Log.d("getUserRole", userData.toString())
                    userData?.role
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        // Return null if the user is not logged in or any error occurs
        return null
    }

}