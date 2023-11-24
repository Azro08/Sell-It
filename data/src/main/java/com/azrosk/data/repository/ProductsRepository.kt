package com.azrosk.data.repository

import android.net.Uri
import com.azrosk.data.model.Category
import com.azrosk.data.model.Product
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage
) {

    private val categoryCollection =
        firestore.collection("category")
    private val productsCollection = firestore.collection("products")

    // Function to fetch all products
    suspend fun getAllProducts(): List<Product> {
        return try {
            val querySnapshot = productsCollection.get().await()
            querySnapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            // Handle exceptions
            emptyList()
        }
    }

    // Function to get products for a specific user
    suspend fun getOtherUsersProducts(userId: String): List<Product> {
        return try {
            val querySnapshot = productsCollection
                .whereNotEqualTo("userId", userId) // Assuming userId field in products
                .get().await()
            querySnapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            // Handle exceptions
            emptyList()
        }
    }

    // Function to update a product
    suspend fun updateProduct(productId: String, updatedProduct: Product) {
        try {
            productsCollection
                .document(productId)
                .set(updatedProduct)
                .await()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    // Function to add a new product
    suspend fun addProduct(newProduct: Product): String {
        return try {
            productsCollection
                .document(newProduct.id)
                .set(newProduct)
                .await()
            "Done"
        } catch (e: FirebaseException) {
            // Handle exceptions
            e.message.toString()
        }
    }

    // Function to delete a product

    suspend fun uploadImageAndGetUri(userId: String, imageUris: List<Uri>): List<String>? {
        val imageUrlList = mutableListOf<String>()
        return try {
            for (imageUri in imageUris) {
                val imageFilename = "product_images/$userId/${UUID.randomUUID()}.jpg"
                val imageRef = storage.reference.child(imageFilename)
                imageRef.putFile(imageUri).await()

                try {
                    val imageUrl = imageRef.downloadUrl.await()
                    imageUrlList.add(imageUrl.toString())
                } catch (e: Exception) {
                    null
                }

            }
            imageUrlList
        } catch (e: Exception) {
            null
        }

    }

    suspend fun deleteProduct(productId: String) {
        try {
            firestore.collection("products")
                .document(productId)
                .delete()
                .await()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    // Function to get products for the current user
    suspend fun getCurrentUsersProducts(): List<Product> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: ""
        return getOtherUsersProducts(currentUserId)
    }

    suspend fun getCategoryList(): List<Category>? {
        val categoryList = mutableListOf<Category>()
        return try {
            val querySnapshot = categoryCollection.get().await()
            for (document in querySnapshot.documents) {
                val category = document.toObject(Category::class.java)
                category?.let {
                    categoryList.add(it)
                }
            }
            categoryList
        } catch (e: Exception) {
            null
        }
    }

}
