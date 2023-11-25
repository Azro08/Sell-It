package com.azrosk.data.model

import android.net.Uri

data class Product(
    val id : String = "",
    val userId : String = "",
    val name : String = "",
    val price : Double = 0.0,
    val description : String = "",
    val imagesUrl : List<String> = emptyList(),
    val category : String = "",
    val quantity : Int = 0,
    val state : String = "", //old/new
    val date : String = "",
    val reviews : List<Review> = emptyList(),
)
