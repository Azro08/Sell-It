package com.azrosk.sell_it.users.notification

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.azrosk.data.model.Order
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentOrderDetailsBinding
import com.azrosk.sell_it.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : DialogFragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private var orderAmount = 0
    private var leftAmount = 0
    private var prodId = ""
    @Inject lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }

        val orderId = arguments?.getString(Constants.ORDER_ID) ?: ""

        Log.d("OrderDetailsFragment", "productId: $orderId")
        getOrderDetails(orderId)
        binding.buttonApproveOrder.setOnClickListener {
            updateStatus(orderId, "Принят")
        }

        binding.buttonDenyOrder.setOnClickListener {
            updateStatus(orderId, "Отклонен")
        }

        return builder.create()
    }

    private fun updateStatus(orderId: String, status: String) {
        lifecycleScope.launch {
            if (status == "Принят") {
                viewModel.updateOrderStatus(orderId, status)
                viewModel.orderStatus.collect {
                    if (it != null) {
                        Toast.makeText(requireContext(), "Принят", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            } else if (status == "Отклонен") {
                viewModel.updateOrderStatus(orderId, status)
                viewModel.orderStatus.collect {
                    if (it != null) {
                        updateAmount()
                    }
                }
            }

        }
    }

    private fun updateAmount() {
        val newAmount = orderAmount + leftAmount
        viewModel.updateProd(prodId, newAmount)
        dismiss()
    }

    private fun getOrderDetails(orderId: String) {
        lifecycleScope.launch {
            viewModel.getOrderDetails(orderId)
            viewModel.orderDetails.collect {
                if (it != null) {
                    displayDetails(it)
                    orderAmount = it.amount
                    leftAmount = it.leftAmount
                    prodId = it.productId
                }
            }
        }
    }

    private fun displayDetails(order: Order) {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        if (order.status == "pending" && order.ownerId == uid) {
            binding.buttonDenyOrder.visibility = View.VISIBLE
            binding.buttonApproveOrder.visibility = View.VISIBLE
        } else if (order.status == "pending"){
            binding.buttonDenyOrder.text = getString(R.string.cancel)
            binding.buttonDenyOrder.visibility = View.VISIBLE
        }


        binding.textViewOrderDetailsAmount.text = order.amount.toString()
        binding.textViewOrderDetailsNote.text = order.note
        binding.textViewOrderDetailsTotalPrice.text = order.totalPrice.toString()
    }

}