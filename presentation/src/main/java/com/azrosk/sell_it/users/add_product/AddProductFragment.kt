package com.azrosk.sell_it.users.add_product

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentAddProductBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private val binding by viewBinding(FragmentAddProductBinding::bind)

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: AddProductViewModel by viewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri = Uri.parse("")
    private var imageUrls = mutableListOf<String>()
    private val imagesList = mutableListOf<Uri>()
    private var imagesPagerAdapter: ProductImagesPagerAdapter? = null
    private val productStateList = listOf("Новое", "Б/у")
    private val categoryList = arrayListOf("")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getCategories()
        binding.buttonSave.setOnClickListener {
            if (areAllFieldsFilled()) {
                if (imagesList.isEmpty()) saveProduct()
                else uploadImage()
            } else Toast.makeText(requireContext(), getString(R.string.fill_upFields), Toast.LENGTH_SHORT)
                .show()
        }
        if (imagesList.size > 5) binding.buttonAddImage.visibility = View.GONE
        else binding.buttonAddImage.visibility = View.VISIBLE
        binding.buttonAddImage.setOnClickListener {
            addImages()
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

    private fun addImages() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setMediaPicker()
    }

    private fun setMediaPicker() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
                imagesList.add(imageUri)
                imagesPagerAdapter = ProductImagesPagerAdapter(requireContext(), imagesList) {
                    imagesList.removeAt(it)
                    imagesPagerAdapter?.notifyDataSetChanged()
                    binding.viewPagerProductImages.adapter = imagesPagerAdapter
                }
                binding.viewPagerProductImages.adapter = imagesPagerAdapter
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun uploadImage() {
        lifecycleScope.launch {
            Log.d("ImageUri", imageUri.toString())
            viewModel.uploadImageAndGetUri(firebaseAuth.currentUser?.uid!!, imagesList)
            viewModel.imageUploaded.collect { state ->
                Log.d("ImagesSiz", imagesList.size.toString())
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        Log.d("ImageUri", state.data.toString())
                        imageUrls.addAll(state.data!!)
                        saveProduct()
                    }

                    is ScreenState.Error -> {
                        binding.buttonSave.visibility = View.VISIBLE
                        binding.loadingDetailsGif.visibility = View.GONE
                        Log.d("ImageUriError", state.message.toString())
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun saveProduct() {
        binding.buttonSave.visibility = View.GONE
        binding.loadingDetailsGif.visibility = View.VISIBLE
        val productName = binding.editTextProductName.text.toString()
        val description = binding.editTextDes.text.toString()
        val price = binding.editTextPrice.text.toString().toDouble()
        val amount = binding.editTextAmount.text.toString().toInt()
        val state = binding.spinnerState.selectedItem.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val userId = firebaseAuth.currentUser?.uid
        val newProduct = Product(
            id = Constants.generateRandomId(),
            name = productName,
            description = description,
            price = price,
            amount = amount,
            state = state,
            category = category,
            userId = userId!!,
            imagesUrl = imageUrls,
        )
        lifecycleScope.launch {
            viewModel.addProduct(newProduct)
            viewModel.productAdded.collect {
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