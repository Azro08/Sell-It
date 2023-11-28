package com.azrosk.sell_it.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constants {
    const val SHARED_PREF_NAME = "user_pref"
    const val USER_KEY = "user_key"
    const val ROLE_KEY = "role_key"
    const val ADMIN = "admin"
    const val USER = "user"
    const val PRODUCT_ID = "product_id"
    const val ORDER_ID = "order_id"
    const val USER_ID = "user_id"
    const val EMAIL = "email"
    const val LANGUAGE_KEY = "language_key"
    const val CHAT_NAME = "chat_name"
    const val CHAT_UID = "chat_uid"
    const val CHAT_IMAGE = "chat_image"

    fun generateRandomId(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random =
            Random(System.currentTimeMillis()) // Seed the random number generator with the current time

        val randomString = StringBuilder(28)

        for (i in 0 until 28) {
            val randomIndex = random.nextInt(characters.length)
            randomString.append(characters[randomIndex])
        }

        return randomString.toString()
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis() / 1000 // Get current timestamp in seconds
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

}