package com.azrosk.data.repository

import com.azrosk.data.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    ){

    private val ordersCollection = firestore.collection("orders")

    suspend fun orderProduct(order: Order) : String{
        return try {
            ordersCollection.document().set(order).await()
            "Done"
        } catch (e: Exception){
            e.message.toString()
        }
    }

    suspend fun getOrders() : List<Order>{
        val userId = firebaseAuth.currentUser?.uid
        val querySnapshot: QuerySnapshot = ordersCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .await()

        return querySnapshot.documents.map { document ->
            document.toObject(Order::class.java)
                ?: throw IllegalStateException("Error converting document")
        }
    }



}