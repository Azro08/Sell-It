package com.azrosk.sell_it.users.order_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Order
import com.azrosk.data.model.Product
import com.azrosk.data.repository.OrderRepository
import com.azrosk.data.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderProductViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productsRepository: ProductsRepository
) : ViewModel(){

    private val _order = MutableStateFlow("")
    val order = _order

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product

    fun getProduct(id : String) = viewModelScope.launch {
        productsRepository.getProductDetails(id).let {
            _product.value = it
        }
    }

    fun makeOrder(order: Order) = viewModelScope.launch {
        orderRepository.orderProduct(order).let {
            _order.value = it
        }
    }

    fun updateProd(productId: String, amount: Int) = viewModelScope.launch{
        val updatedFields = mapOf("amount" to amount)
        productsRepository.updateProduct(productId, updatedFields)
    }


}