package com.azrosk.data.repository

import android.util.Log
import com.azrosk.data.model.Product
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,

    ) {

    private val favoritesCollection = firestore.collection("favorites")

    suspend fun saveProductToFavorites(product: Product): String {
        return try {
            val userId = firebaseAuth.currentUser?.uid
            Log.d("FavRepos", userId.toString())
            val favProductId = userId + product.id
            Log.d("FavRepos", favProductId)
            favoritesCollection.document(favProductId).set(product).await()
            "Done"
        } catch (e: FirebaseException) {
            e.message.toString()
        }
    }

    suspend fun getFavoriteProducts(): List<Product> {
        val currentUserID = firebaseAuth.currentUser?.uid ?: ""
        val querySnapshot = favoritesCollection.get().await()

        return querySnapshot.documents.mapNotNull { document ->
            val documentID = document.id
            val userIdLength = currentUserID.length

            if (documentID.length > userIdLength && documentID.startsWith(currentUserID)) {
                val extractedUserId = documentID.substring(0, userIdLength)
                if (extractedUserId == currentUserID) {
                    document.toObject(Product::class.java)
                } else {
                    null // Document does not belong to the current user
                }
            } else {
                null // Document format doesn't match the user ID structure
            }
        }.filterNotNull()
    }


    suspend fun removeProductFromFavorites(productId: String): Boolean {
        val userId = firebaseAuth.currentUser?.uid ?: ""
        val favProductId = userId + productId

        return try {
            favoritesCollection.document(favProductId).delete().await()
            true // Deletion successful
        } catch (e: FirebaseException) {
            Log.e("FavoriteRep", "Error removing product from favorites: ${e.message}")
            false // Deletion failed
        }
    }


}