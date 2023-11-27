package com.azrosk.data.repository

import android.util.Log
import com.azrosk.data.model.FeedBack
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedBackRepository @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val feedbackCollection = firestore.collection("feedBacks")
    suspend fun sendFeedback(feedback: FeedBack): String {
        return try {
            feedbackCollection
                .add(feedback)
                .await()

            "Done"
        } catch (e: Exception) {
            Log.d("sendFeedback", e.message.toString())
            e.message.toString()
        }
    }

    suspend fun getFeedBacks(): List<FeedBack> {
        return try {
            feedbackCollection
                .get()
                .await()
                .toObjects(FeedBack::class.java)
        } catch (e: FirebaseException) {
            emptyList()
        }
    }

}