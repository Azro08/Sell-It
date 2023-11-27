package com.azrosk.domain.repository

interface UsersRepository {
    suspend fun deleteUser(userId : String) : String
    suspend fun deleteUsersProducts(uid: String)
}