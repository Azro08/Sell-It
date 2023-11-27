package com.azrosk.sell_it.users.edit_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Category
import com.azrosk.data.model.Product
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ScreenState<Product>?>(ScreenState.Loading())
    val product = _product

    private val _editProduct = MutableStateFlow("")
    val editProduct = _editProduct

    private val _categoryList =
        MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList

    init {
        getCategoryList()
    }

    fun getProduct(id: String) = viewModelScope.launch {
        productsRepository.getProductDetails(id).let {
            if (it != null) _product.value = ScreenState.Success(it)
            else _product.value = ScreenState.Error("Error")
        }
    }

    fun editProduct(productId: String, updatedProduct: Map<String, Any>) =
        viewModelScope.launch {
            productsRepository.updateProduct(productId, updatedProduct).let {
                if (it.isNotEmpty()) _editProduct.value = it
                else _editProduct.value = "Error"
            }
        }

    private fun getCategoryList() = viewModelScope.launch {
        productsRepository.getCategoryList().let {
            if (!it.isNullOrEmpty()) _categoryList.value = ScreenState.Success(it)
            else _categoryList.value = ScreenState.Error("Error Loading categories")
        }
    }

}