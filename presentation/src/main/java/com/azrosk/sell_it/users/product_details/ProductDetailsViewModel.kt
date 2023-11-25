package com.azrosk.sell_it.users.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Product
import com.azrosk.data.model.Review
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.data.repository.UsersRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _productDetails = MutableStateFlow<ScreenState<Product>?>(ScreenState.Loading())
    val productDetails = _productDetails

    private val _reviewText = MutableStateFlow<ScreenState<String>?>(ScreenState.Loading())
    val reviewText = _reviewText

    private val _userImg = MutableStateFlow("")
    val userImg = _userImg

    fun getProductDetails(productId: String) = viewModelScope.launch {
        productsRepository.getProductDetails(productId).let {
            if (it != null) _productDetails.value = ScreenState.Success(it)
            else _productDetails.value = ScreenState.Error("Error loading details")
        }
    }

    fun sendReview(productId: String, review: Review) = viewModelScope.launch {
        productsRepository.submitReview(productId, review).let {
            if (it == "Done") _reviewText.value = ScreenState.Success(it)
            else _reviewText.value = ScreenState.Error(it)
        }
        getProductDetails(productId)
    }

    fun getUserImage(uid: String) = viewModelScope.launch {
        usersRepository.getUser(uid).let {
            if (it != null) _userImg.value = it.imageUrl
            else _userImg.value = ""
        }
    }

}