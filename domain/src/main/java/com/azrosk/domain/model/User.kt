package com.azrosk.domain.model

data class User(
    var id : String = "",
    val email : String = "",
    val role : String = "",
    val fullName : String = "",
    val imageUrl : String = "",
    val phoneNumber : String = "",
    val address : String = "",
)
