package com.azrosk.data.repository

import android.net.Uri
import android.util.Log
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
    private val favoritesCollection = firestore.collection("favorites")

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
    suspend fun getOtherUsersProducts(category : String): List<Product> {
        val userId = firebaseAuth.currentUser?.uid ?: ""
        return try {
            val querySnapshot = productsCollection
                .whereNotEqualTo("userId", userId)
                .whereEqualTo("category", category)
                .get().await()
            querySnapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            // Handle exceptions
            Log.d("ProductsRepository", "${e.message}")
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


    suspend fun saveProductToFavorites(product: Product) : String {
        return try {
            val userId = firebaseAuth.currentUser?.uid
            val favProductId = userId + product.id
            favoritesCollection.document(favProductId).set(product).await()
            "Done"
        }catch (e : FirebaseException){
            e.message.toString()
        }
    }


}
