package com.azrosk.sell_it.util

import android.content.Context
import com.azrosk.data.model.Users
import com.azrosk.domain.model.User

class AuthManager(private val context: Context) {

    fun isLoggedIn(): Boolean {
        val authToken = getUser()
        return authToken.isNotEmpty()
    }

    fun getUser(): String {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.USER_KEY, "") ?: ""
    }

    fun saveUer(email: String) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.USER_KEY, email).apply()
    }

    fun removeUser() {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(Constants.USER_KEY).apply()
    }

    fun getRole(): String {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.ROLE_KEY, "") ?: ""
    }

    fun saveRole(role: String) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.ROLE_KEY, role).apply()
    }

    fun removeRole() {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(Constants.ROLE_KEY).apply()
    }

}
