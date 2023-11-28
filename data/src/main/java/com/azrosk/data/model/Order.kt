package com.azrosk.data.model

data class Order (
    val id : String = "",
    val productId : String = "",
    val productName : String = "",
    val orderedBy : String = "", //email
    val orderedById: String = "",
    val ownerId : String = "",
    val date : String = "",
    val status : String = "", //pending/accepted
    val amount : Int = 0,
    val leftAmount : Int = 0,
    val totalPrice : Double = 0.0,
    val paymentType : String = "",
    val deliveryAddress : String = "",
    val deliveryMethod : String = "",
    val paymentMethod : String = "",
    val note : String = "",
)