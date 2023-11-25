package com.azrosk.sell_it.users.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Product
import com.azrosk.data.repository.FavoritesRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<ScreenState<List<Product?>>>(ScreenState.Loading())
    val favorites = _favorites

    private val _productRemoved = MutableStateFlow<Boolean>(false)
    val productRemoved = _productRemoved

    init {
        getFavorites()
    }

    fun refresh() {
        getFavorites()
    }

    private fun getFavorites() = viewModelScope.launch {
        try {
            favoritesRepository.getFavoriteProducts().let {
                _favorites.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _favorites.value = ScreenState.Error(e.message.toString())
        }
    }

    fun removeFavorite(productId: String) = viewModelScope.launch {
        favoritesRepository.removeProductFromFavorites(productId).let {
            _productRemoved.value = it
            getFavorites()
        }
    }

}