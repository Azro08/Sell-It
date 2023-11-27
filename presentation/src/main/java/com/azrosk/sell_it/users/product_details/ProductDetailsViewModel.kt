package com.azrosk.sell_it.users.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Product
import com.azrosk.data.model.Review
import com.azrosk.data.model.Users
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.data.repository.UsersRepositoryImpl
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val usersRepositoryImpl: UsersRepositoryImpl
) : ViewModel() {

    private val _productDetails = MutableStateFlow<ScreenState<Product>?>(ScreenState.Loading())
    val productDetails = _productDetails

    private val _reviewText = MutableStateFlow<ScreenState<String>?>(ScreenState.Loading())
    val reviewText = _reviewText

    private val _user = MutableStateFlow(Users())
    val user = _user

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

    fun getUser(uid: String) = viewModelScope.launch {
        usersRepositoryImpl.getUser(uid).let {
            if (it != null) _user.value = it
            else _user.value = Users()
        }
    }

}