package com.azrosk.data.repository

import android.net.Uri
import android.util.Log
import com.azrosk.data.api.UserService
import com.azrosk.data.model.Users
import com.azrosk.domain.repository.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val api: UserService,

    ) : UsersRepository {

    private val usersCollection = firestore.collection("users")
    suspend fun getUsers(): List<Users> {
        try {
            val querySnapshot = usersCollection.get().await()
            val usersList = mutableListOf<Users>()
            for (document in querySnapshot) {
                val user = document.toObject(Users::class.java)
                if (user.role != "admin") {
                    usersList.add(user)
                }
            }
            return usersList
        } catch (e: Exception) {
            // Handle any errors or exceptions here
            throw e
        }
    }

    suspend fun deleteUser(uid: String) = api.deleteUser(uid)


    override suspend fun deleteUsersProducts(uid: String) {
        val firestore = FirebaseFirestore.getInstance()
        val productsCollection = firestore.collection("products")

        val query = productsCollection.whereEqualTo("userId", uid)

        val querySnapshot: QuerySnapshot = query.get().await()

        val deletionTasks: MutableList<Task<Void>> = mutableListOf()
        for (document in querySnapshot.documents) {
            val deletionTask = productsCollection.document(document.id).delete()
            deletionTasks.add(deletionTask)
        }

        try {
            Tasks.await(Tasks.whenAll(deletionTasks))
        } catch (e: Exception) {
            Log.d("UsersRep", e.message.toString())
        }
    }


    suspend fun getUser(userId: String): Users? {
        val userDocument = usersCollection.document(userId)
        val documentSnapshot = userDocument.get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(Users::class.java)
        } else {
            null
        }
    }

    override suspend fun deleteAccount(userId: String): String {
        val userDocument = usersCollection.document(userId)
        return try {
            userDocument.delete().await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun saveUser(user: Users): String {
        return try {
            // Firestore-specific logic here
            val userDocRef = usersCollection.document(user.id)
            userDocRef.set(user).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun updateUserFields(
        userId: String,
        updatedFields: Map<String, Any>,
        password: String,
        oldPassword: String = "",
        email: String = ""
    ): String {
        val userDocument = usersCollection.document(userId)

        val result = runCatching {
            userDocument.update(updatedFields).await()
            if (password.isNotEmpty() && oldPassword.isNotEmpty() && email.isNotEmpty()) {
                runCatching {
                    firebaseAuth.signInWithEmailAndPassword(email, oldPassword).await()
                }.onFailure {
                    return it.message.toString()
                }
                runCatching {
                    firebaseAuth.currentUser?.updatePassword(password)?.await()
                }.onFailure {
                    return it.message.toString()
                }
            }
            "Done"
        }.onFailure {
            return it.message.toString()
        }

        return result.getOrThrow()
    }


    suspend fun uploadImageAndGetUri(userId: String, imageUri: Uri): Uri? {
        return try {
            val imageFilename = "profile_images/$userId/${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child(imageFilename)

            imageRef.putFile(imageUri).await()

            // Use await to wait for the task to complete
            try {
                imageRef.downloadUrl.await()
            } catch (e: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


}
