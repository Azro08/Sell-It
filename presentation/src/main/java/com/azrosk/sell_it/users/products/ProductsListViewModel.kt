package com.azrosk.sell_it.users.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Category
import com.azrosk.data.model.Product
import com.azrosk.data.model.Users
import com.azrosk.data.repository.FavoritesRepository
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.data.repository.UsersRepositoryImpl
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val usersRepositoryImpl: UsersRepositoryImpl
) : ViewModel() {

    private val _productsList = MutableStateFlow<ScreenState<List<Product>?>>(ScreenState.Loading())
    val productsList = _productsList

    private val _categoryList = MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList

    private val _addedToFav = MutableStateFlow("")
    val addedToFav = _addedToFav

    private val _user = MutableStateFlow<Users?>(null)
    val user = _user

    fun getUser(userId : String) = viewModelScope.launch {
        usersRepositoryImpl.getUser(userId).let {
            if (it != null) _user.value = it
        }
    }

    init {
        getCategories()
    }

    fun refresh(category: String){
        getProducts(category)
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

    private fun getCategories() = viewModelScope.launch {
        productsRepository.getCategoryList().let {
            if (!it.isNullOrEmpty()) _categoryList.value = ScreenState.Success(it)
            else _categoryList.value = ScreenState.Error("No categories found")
        }
    }

    fun addProductToFavorites(product: Product) = viewModelScope.launch {
        favoritesRepository.saveProductToFavorites(product).let {
            _addedToFav.value = it
        }
    }

    fun getProducts(category: String) = viewModelScope.launch {

        productsRepository.getOtherUsersProducts(category).let {
            if (it.isNotEmpty()) _productsList.value = ScreenState.Success(it)
            else _productsList.value = ScreenState.Error("Продукты не найдены")
        }

    }

}