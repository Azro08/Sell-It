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
    const val AMOUNT = "amount"

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

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

}