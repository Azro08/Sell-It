package com.azrosk.sell_it.admin.produts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Product
import com.azrosk.data.model.Users
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.data.repository.UsersRepositoryImpl
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val usersRepositoryImpl: UsersRepositoryImpl
) : ViewModel() {

    private val _productsList = MutableStateFlow<ScreenState<List<Product>?>>(ScreenState.Loading())
    val productsList = _productsList

    private val _deleteState = MutableStateFlow("")
    val deleteState = _deleteState

    private val _user = MutableStateFlow<Users?>(null)
    val user = _user

    fun getUser(userId: String) = viewModelScope.launch {
        usersRepositoryImpl.getUser(userId).let {
            if (it != null) _user.value = it
        }
    }

    init {
        getAllProducts()
    }

    fun refresh() {
        getAllProducts()
    }

    fun filterProductList(query: String): List<Product> {
        return when (val currentState = _productsList.value) {
            is ScreenState.Success -> {
                currentState.data?.filter { product ->
                    product.name.contains(query, ignoreCase = true)
                } ?: emptyList()
            }

            else -> emptyList()
        }
    }

    private fun getAllProducts() = viewModelScope.launch {

        productsRepository.getAllProducts().let {
            if (it.isNotEmpty()) _productsList.value = ScreenState.Success(it)
            else _productsList.value = ScreenState.Error("Продукты не найдены")
        }

    }

    fun deleteProduct(productId: String) = viewModelScope.launch {
        productsRepository.deleteProduct(productId).let {
            _deleteState.value = it
            getAllProducts()
        }
    }

}