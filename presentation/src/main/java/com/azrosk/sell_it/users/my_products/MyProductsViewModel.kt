package com.azrosk.sell_it.users.my_products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Category
import com.azrosk.data.model.Product
import com.azrosk.data.model.Users
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _productsList = MutableStateFlow<ScreenState<List<Product>?>>(ScreenState.Loading())
    val productsList = _productsList

    private val _categoryList =
        MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList

    private val _deleted = MutableStateFlow("")
    val deleted = _deleted

    private val _user = MutableStateFlow<Users?>(null)
    val user = _user

    init {
        getCategories()
    }

    fun refresh(category: String) {
        getMyProducts(category)
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

    suspend fun deleteProduct(productId: String) {
        productsRepository.deleteProduct(productId).let {
            _deleted.value = it
        }
    }

    private fun getCategories() = viewModelScope.launch {
        productsRepository.getCategoryList().let {
            if (!it.isNullOrEmpty()) _categoryList.value = ScreenState.Success(it)
            else _categoryList.value = ScreenState.Error("No categories found")
        }
    }

    fun getMyProducts(category: String) = viewModelScope.launch {

        productsRepository.getMyProducts(category).let {
            if (it.isNotEmpty()) _productsList.value = ScreenState.Success(it)
            else _productsList.value = ScreenState.Error("No products found")
        }

    }

}