package com.azrosk.sell_it.users.order_product

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Order
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentOrderProductBinding
import com.azrosk.sell_it.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderProductFragment : Fragment(R.layout.fragment_order_product) {
    private val binding by viewBinding(FragmentOrderProductBinding::bind)
    private val viewModel: OrderProductViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productId: String
    private var orderAmount = 1
    private var totalAmount = 1
    private var totalPrice = 0.0
    private var ownerId: String = ""
    private var productName = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productId = arguments?.getString(Constants.PRODUCT_ID) ?: ""
        getProductDetails(productId)
        binding.buttonSubmitOrder.setOnClickListener {
            if (allFieldsAreFilled()) submitOrder()
            else  Toast.makeText(context, getString(R.string.fill_upFields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProductDetails(productId: String) {
        lifecycleScope.launch {
            viewModel.getProduct(productId)
            viewModel.product.collect {
                if (it != null) {
                    totalAmount = it.amount
                    val price = it.price
                    ownerId = it.userId
                    productName = it.name
                    binding.textViewProdName.text = it.name
                    totalPrice = it.price
                    binding.textViewOrderTotalPrice.text = totalPrice.toString()
                    binding.buttonInc.setOnClickListener {
                        if (orderAmount < totalAmount) {
                            orderAmount++
                            totalPrice += price
                            val priceFormatted = String.format("%.2f", totalPrice)
                            val priceText = "$priceFormatted руб"
                            binding.textViewOrderTotalPrice.text = priceText
                            binding.textViewOrderAmount.text = orderAmount.toString()
                        }
                    }

                    binding.buttonDec.setOnClickListener {
                        if (orderAmount > 1) {
                            orderAmount--
                            totalPrice -= price
                            val priceFormatted = String.format("%.2f", totalPrice)
                            val priceText = "$priceFormatted руб"
                            binding.textViewOrderTotalPrice.text = priceText
                            binding.textViewOrderAmount.text = orderAmount.toString()
                        }
                    }

                }
            }
        }
    }

    private fun allFieldsAreFilled(): Boolean {
        val fullName = binding.editTextFullName.text.toString()
        val deliveryMethod = getDeliveryMethod()
        val paymentMethod = getPaymentMethod()
        val deliveryAddress = binding.editTextAddress.text.toString()

        return fullName.isNotEmpty() && deliveryMethod.isNotEmpty() && paymentMethod.isNotEmpty() && deliveryAddress.isNotEmpty()

    }

    private fun submitOrder() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        val fullName = binding.editTextFullName.text.toString()
        var note = binding.editTextNote.text.toString()
        if (note.isEmpty()) note = ""
        val deliveryAddress = binding.editTextAddress.text.toString()
        val deliveryMethod = getDeliveryMethod()
        val paymentMethod = getPaymentMethod()
        val status = "pending"

        val order = Order(
            id = Constants.generateRandomId(),
            productId = productId,
            orderedBy = fullName,
            ownerId = ownerId,
            deliveryAddress = deliveryAddress,
            date = Constants.getCurrentDate(),
            deliveryMethod = deliveryMethod,
            paymentMethod = paymentMethod,
            status = status,
            orderedById = uid,
            amount = orderAmount,
            totalPrice = totalPrice,
            note = note,
            productName = productName
        )
        lifecycleScope.launch {
            viewModel.makeOrder(order)
            viewModel.order.collect {
                if (it == "Done") {
                    val leftAmount = totalAmount - orderAmount
                    viewModel.updateProd(productId, leftAmount)
                    findNavController().popBackStack()
                }
                else Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getPaymentMethod(): String {
        return if (binding.radioButtonCreditCard.isChecked) {
            "Credit Card"
        } else {
            "Cash"
        }
    }

    private fun getDeliveryMethod(): String {
        return if (binding.rbNextDay.isChecked) "Next day"
        else if (binding.rbSameDay.isChecked) "Same day"
        else "Pickup"
    }

}