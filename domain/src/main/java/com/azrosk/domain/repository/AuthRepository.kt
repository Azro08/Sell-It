package com.azrosk.domain.repository

import com.azrosk.domain.model.User

interface AuthRepository {

    suspend fun login(email: String, password: String): String

    suspend fun signup(email: String, password: String): String

    suspend fun saveUser(user: User): String

    suspend fun deleteAccount(uid: String) : String

    suspend fun getUserRole(): String?
}