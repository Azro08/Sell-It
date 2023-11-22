package com.azrosk.data.api

import retrofit2.http.DELETE
import retrofit2.http.Path

interface UserService {
    @DELETE("/api/deleteUser/{uid}")
    suspend fun deleteUser(@Path("uid") uid: String): String
}