package com.azrosk.sell_it.admin.produts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentAdminProductsBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AdminProductsFragment : Fragment(R.layout.fragment_admin_products) {
    private val binding by viewBinding(FragmentAdminProductsBinding::bind)
    private val viewModel: AdminProductsViewModel by viewModels()
    private var productsRvAdapter: AdminProductsRvAdapter? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getProducts()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        search()
    }


    private fun search() {
        binding.editTextSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                performSearch(searchText)
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.filterProductList(query)
        productsRvAdapter?.updateUsersList(filteredList)
    }


    private fun getProducts() {
        lifecycleScope.launch {
            viewModel.productsList.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        binding.loadingGif.visibility = View.GONE
                        if (!state.data.isNullOrEmpty()) displayProducts(state.data)
                        else handleError(getString(R.string.no_products_found))
                    }
                }
            }
        }
    }

    private fun displayProducts(productList: List<Product>) {
        binding.textViewError.visibility = View.GONE
        binding.rvProducts.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE
        productsRvAdapter =
            AdminProductsRvAdapter(productList, { navToDetails(it) }, { deleteProduct(it) })
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = productsRvAdapter
    }

    private fun navToDetails(product: Product) {
        findNavController().navigate(
            R.id.nav_list_to_prod_details,
            bundleOf(Pair(Constants.PRODUCT_ID, product.id))
        )
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            viewModel.deleteProduct(product.id)
            viewModel.deleteState.collect {
                if (it == "Done") Toast.makeText(
                    requireContext(),
                    getString(R.string.deleted),
                    Toast.LENGTH_SHORT
                )
                    .show()
                else Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleError(msg: String) {
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE
        binding.textViewError.text = msg
    }

    override fun onDestroy() {
        super.onDestroy()
        productsRvAdapter = null
    }
}