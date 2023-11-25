package com.azrosk.sell_it.users.product_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Product
import com.azrosk.data.model.Review
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentProductDetailsBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {
    private val binding by viewBinding(FragmentProductDetailsBinding::bind)
    private val viewModel: ProductDetailsViewModel by viewModels()
    private var profileImgUrl = ""
    private var ownerPhoneNum = ""
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private var reviewsRvAdapter : ReviewsRvAdapter? = null
    private var imagesViewPagerAdapter : ProductDetailsImagesPagerAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val productId = arguments?.getString(Constants.PRODUCT_ID) ?: ""
        getUsersData()
        getDetails(productId)
        binding.buttonSendFeedback.setOnClickListener {
            if (binding.editTextReview.text.toString().isNotEmpty()) sendFeedback(productId)
        }
        binding.buttonOrder.setOnClickListener {
            findNavController().navigate(R.id.nav_details_order, bundleOf(Pair(Constants.PRODUCT_ID, productId)))
        }
    }

    private fun sendFeedback(productId: String) {
        binding.buttonSendFeedback.visibility = View.GONE
        binding.sendLoadingGif.visibility = View.VISIBLE
        val reviewText = binding.editTextReview.text.toString()
        val userImg = profileImgUrl
        val date = Constants.getCurrentDate()
        val email = firebaseAuth.currentUser?.email ?: ""
        val newReview = Review(
            userEmail = email,
            userIcon = userImg,
            date = date,
            review = reviewText
        )
        lifecycleScope.launch {

            viewModel.sendReview(productId, newReview)
            viewModel.reviewText.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        binding.buttonSendFeedback.visibility = View.VISIBLE
                        binding.sendLoadingGif.visibility = View.GONE
                    }

                    is ScreenState.Success -> {
                        binding.buttonSendFeedback.visibility = View.VISIBLE
                        binding.sendLoadingGif.visibility = View.GONE
                        if (state.data == "Done") {
                            Toast.makeText(context, "Отзыв отправлен", Toast.LENGTH_SHORT).show()
                        }

                    }

                    else -> {}

                }
            }

        }
    }

    private fun getUsersData() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        lifecycleScope.launch {
            viewModel.getUser(uid)
            viewModel.user.collect {
                profileImgUrl = it.imageUrl
                ownerPhoneNum = it.phoneNumber
            }
        }
    }

    private fun getDetails(productId: String) {
        lifecycleScope.launch {

            viewModel.getProductDetails(productId)
            viewModel.productDetails.collect { state ->

                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        if (state.data != null) displayDetails(state.data)
                        else handleError(state.message.toString())
                    }

                    else -> {}
                }

            }

        }
    }

    private fun displayDetails(product: Product) {
        binding.loadingGif.visibility = View.GONE
        binding.detailsLayout.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.textViewProductName.text = product.name
        binding.textViewDescription.text = product.description
        val price = "${product.price} руб."
        binding.textViewPrice.text = price
        binding.textViewDate.text = product.date
        binding.textViewAmount.text  = product.amount.toString()

        binding.buttonCallOwner.setOnClickListener {
            callOwner()
        }

        imagesViewPagerAdapter = ProductDetailsImagesPagerAdapter(requireContext() ,product.imagesUrl)

        reviewsRvAdapter = ReviewsRvAdapter(product.reviews)
        binding.recyclerViewReviews.setHasFixedSize(true)
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewReviews.adapter = reviewsRvAdapter
    }

    private fun callOwner() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ownerPhoneNum, null))
        startActivity(intent)
    }

    private fun handleError(msg: String) {
        binding.loadingGif.visibility = View.GONE
        binding.detailsLayout.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = msg
    }

    override fun onDestroy() {
        super.onDestroy()
        reviewsRvAdapter = null
        imagesViewPagerAdapter = null
    }

}