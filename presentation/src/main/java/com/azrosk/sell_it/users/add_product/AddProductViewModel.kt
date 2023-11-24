package com.azrosk.sell_it.users.add_product

import android.net.Uri
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
class AddProductViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel(){

    private val _productAdded = MutableStateFlow("")
    val productAdded = _productAdded

    private val _imageUploaded = MutableStateFlow<ScreenState<List<Uri>?>>(ScreenState.Loading())
    val imageUploaded: MutableStateFlow<ScreenState<List<Uri>?>> = _imageUploaded

    private val _categoryList =
        MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList

    init {
        getCategoryList()
    }

    private fun getCategoryList() = viewModelScope.launch {
        productsRepository.getCategoryList().let {
            if (!it.isNullOrEmpty()) _categoryList.value = ScreenState.Success(it)
            else _categoryList.value = ScreenState.Error("Error Loading categories")
        }
    }

    fun uploadImageAndGetUri(userId: String, imageUri: List<Uri>) = viewModelScope.launch {
        try {
            productsRepository.uploadImageAndGetUri(userId, imageUri).let {
                if (it != null) _imageUploaded.value = ScreenState.Success(it)
                else _imageUploaded.value = ScreenState.Error("Error Loading image")
            }
        } catch (e: Exception) {
            _imageUploaded.value = ScreenState.Error(e.message.toString())
        }
    }
    fun addProduct(product: Product) = viewModelScope.launch {

        productsRepository.addProduct(product).let{
            _productAdded.value = it
        }

    }

}