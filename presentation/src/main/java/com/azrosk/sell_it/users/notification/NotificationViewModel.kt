package com.azrosk.sell_it.users.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Order
import com.azrosk.data.repository.OrderRepository
import com.azrosk.data.repository.ProductsRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productsRepository: ProductsRepository
) : ViewModel(){

    private val _orders = MutableStateFlow<ScreenState<List<Order>?>>(ScreenState.Loading())
    val orders = _orders

    private val _orderDetails = MutableStateFlow<Order?>(null)
    val orderDetails = _orderDetails

    private val _orderStatus = MutableStateFlow<String?>(null)
    val orderStatus = _orderStatus

    init {
        getOrders()
    }

    fun refresh() {
        getOrders()
    }

    fun updateProd(productId: String, amount: Int) = viewModelScope.launch{
        val updatedFields = mapOf("amount" to amount)
        productsRepository.updateProduct(productId, updatedFields)
    }

    fun updateOrderStatus(orderId: String, status: String)  = viewModelScope.launch {
        try {
            orderRepository.updateOrderStatus(orderId, status)
            _orderStatus.value = "Done"
        } catch (e: Exception) {
            _orderStatus.value = e.message.toString()
        }
    }

    fun getOrderDetails(orderId: String) = viewModelScope.launch {
        try {
            orderRepository.getOrder(orderId).let {
                Log.d("NotificationViewModel", it.toString())
                _orderDetails.value = it
            }
        } catch (e: Exception) {
            Log.d("NotificationViewModel", e.message.toString())
        }
    }

    private fun getOrders() = viewModelScope.launch {
        orderRepository.getOrders().let {
            if (it.isNotEmpty()) _orders.value = ScreenState.Success(it)
            else _orders.value = ScreenState.Error("No orders found")
        }
    }



}