package com.azrosk.data.repository

import android.util.Log
import com.azrosk.data.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) {

    private val ordersCollection = firestore.collection("orders")

    suspend fun orderProduct(order: Order): String {
        return try {
            ordersCollection.document(order.id).set(order).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun getOrders(): List<Order> {
        val userId = firebaseAuth.currentUser?.uid
        val querySnapshot: QuerySnapshot = ordersCollection
            .whereEqualTo("ownerId", userId)
            .get()
            .await()

        val orders = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Order::class.java)
        }.toMutableList()

        val additionalQuerySnapshot: QuerySnapshot = ordersCollection
            .whereEqualTo("orderedById", userId)
            .get()
            .await()

        val additionalOrders = additionalQuerySnapshot.documents.mapNotNull { document ->
            document.toObject(Order::class.java)
        }

        orders.addAll(additionalOrders)

        return orders
    }

    suspend fun getOrder(orderId: String): Order? {
        val orders = getOrders()
        Log.d("OrderRepository", "orders size: ${orders.size}")
        var myOrder: Order? = null
        for (order in orders) {
            Log.d("OrderRepository", "given id: $orderId,   found id: ${order.id}")
            if (order.id == orderId) {
                myOrder = order
            }
        }
        return myOrder
    }

    suspend fun deleteOrder(orderId: String) {
        ordersCollection.document(orderId).delete().await()
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        ordersCollection.document(orderId).update("status", status).await()
    }


}