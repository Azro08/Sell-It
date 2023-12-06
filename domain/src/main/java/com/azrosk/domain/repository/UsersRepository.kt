package com.azrosk.domain.repository

interface UsersRepository {
    suspend fun deleteAccount(userId : String) : String
    suspend fun deleteUsersProducts(uid: String)
}