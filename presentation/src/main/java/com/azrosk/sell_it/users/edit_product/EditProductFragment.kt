package com.azrosk.sell_it.users.edit_product

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentEditProductBinding
import com.azrosk.sell_it.users.product_details.ProductDetailsImagesPagerAdapter
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProductFragment : Fragment(R.layout.fragment_edit_product) {
    private val binding by viewBinding(FragmentEditProductBinding::bind)
    private var imagesPagerAdapter: ProductDetailsImagesPagerAdapter? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: EditProductViewModel by viewModels()
    private val productStateList = listOf("Новое", "Б/у")
    private val categoryList = arrayListOf("")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val productId = arguments?.getString(Constants.PRODUCT_ID) ?: ""
        getProductDetails(productId)
        getCategories()
        binding.buttonSave.setOnClickListener {
            if (areAllFieldsFilled()) saveProduct(productId)
            else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
        }
        setSpinner()
    }

    private fun setSpinner() {
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            categoryList
        )
        val stateAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            productStateList
        )
        binding.spinnerCategory.adapter = categoryAdapter
        binding.spinnerState.adapter = stateAdapter
    }

    private fun getCategories() {
        lifecycleScope.launch {
            viewModel.categoryList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> Toast.makeText(
                        requireContext(),
                        state.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) {
                            for (category in state.data) {
                                categoryList.add(category.name)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getProductDetails(productId: String) {
        lifecycleScope.launch {
            viewModel.getProduct(productId)
            viewModel.product.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ScreenState.Success -> {
                        if (state.data != null) {
                            val product = state.data
                            binding.editTextProductName.setText(product.name)
                            binding.editTextDes.setText(product.description)
                            binding.editTextPrice.setText(product.price.toString())
                            binding.editTextAmount.setText(product.amount.toString())
                            imagesPagerAdapter = ProductDetailsImagesPagerAdapter(
                                requireContext(),
                                product.imagesUrl
                            )
                            binding.viewPagerProductImages.adapter = imagesPagerAdapter
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun saveProduct(productId: String) {
        binding.buttonSave.visibility = View.GONE
        binding.loadingDetailsGif.visibility = View.VISIBLE
        val productName = binding.editTextProductName.text.toString()
        val description = binding.editTextDes.text.toString()
        val price = binding.editTextPrice.text.toString().toDouble()
        val amount = binding.editTextAmount.text.toString().toInt()
        val state = binding.spinnerState.selectedItem.toString()
        val category = binding.spinnerCategory.selectedItem.toString()

        val updatedProduct = mapOf(
            "name" to productName,
            "description" to description,
            "price" to price,
            "amount" to amount,
            "state" to state,
            "category" to category,
        )

        lifecycleScope.launch {
            viewModel.editProduct(productId, updatedProduct)
            viewModel.editProduct.collect {
                if (it == "Done") {
                    Toast.makeText(requireContext(), getString(R.string.uploaded), Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                } else {
                    binding.buttonSave.visibility = View.VISIBLE
                    binding.loadingDetailsGif.visibility = View.GONE
                    if (it.isNotEmpty()) Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun areAllFieldsFilled(): Boolean {
        val productName = binding.editTextProductName.text.toString()
        val description = binding.editTextDes.text.toString()
        val price = binding.editTextPrice.text.toString()
        val amount = binding.editTextAmount.text.toString()
        val state = binding.spinnerState.selectedItem.toString()
        val category = binding.spinnerCategory.selectedItem.toString()

        return (productName.isNotEmpty() &&
                description.isNotEmpty() &&
                price.isNotEmpty() &&
                amount.isNotEmpty() &&
                state.isNotEmpty() &&
                category.isNotEmpty())
    }

    override fun onDestroy() {
        super.onDestroy()
        imagesPagerAdapter = null
    }


}