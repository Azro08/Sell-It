package com.azrosk.data.model

import com.azrosk.domain.model.User

data class Users(
    val id : String = "",
    val email : String = "",
    val role : String = "",
    val fullName : String = "",
    val imageUrl : String = "",
    val phoneNumber : String = "",
    val address : String = "",
)

fun Users.toUsersDomain() = User(
    id = id,
    email = email,
    role = role,
    fullName = fullName,
    imageUrl = imageUrl,
    phoneNumber = phoneNumber,
    address = address,
)