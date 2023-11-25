package com.azrosk.sell_it.users.products

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Category
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentProductsListBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsListFragment : Fragment(R.layout.fragment_products_list) {
    private val binding by viewBinding(FragmentProductsListBinding::bind)
    private val viewModel: ProductsListViewModel by viewModels()
    private var productsRvAdapter: ProductsRvAdapter? = null
    private var categoryRvAdapter: CategoryRvAdapter? = null
    private var category: String = "Все"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getCategories()
        getProducts()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(category)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getCategories() {
        lifecycleScope.launch {
            viewModel.categoryList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> handleError(state.message.toString())
                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) handleError(state.message.toString())
                        else displayCategories(state.data)
                    }
                }
            }
        }
    }

    private fun displayCategories(productCategories: List<Category>) {
        Log.d("FoodList", "food ${productCategories.size}")
        categoryRvAdapter = CategoryRvAdapter(productCategories) {
            category = it.name
            getProducts()
        }
        binding.rvProductsCategory.setHasFixedSize(true)
        binding.rvProductsCategory.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvProductsCategory.adapter = categoryRvAdapter
    }

    private fun getProducts() {
        lifecycleScope.launch {
            viewModel.getProducts(category)
            viewModel.productsList.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        binding.loadingGif.visibility = View.GONE
                        if (!state.data.isNullOrEmpty()) displayProducts(state.data)
                        else handleError("No products found")
                    }
                }
            }
        }
    }

    private fun displayProducts(productList: List<Product>) {
        binding.textViewError.visibility = View.GONE
        binding.rvProductsCategory.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.VISIBLE
        binding.searchLayout.visibility = View.VISIBLE
        productsRvAdapter = ProductsRvAdapter(productList, { navToDetails(it) }, { saveToFav(it) })
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = productsRvAdapter
    }

    private fun navToDetails(product: Product) {
        findNavController().navigate(R.id.nav_list_to_prod_details, bundleOf(Pair(Constants.PRODUCT_ID, product.id)))
    }

    private fun saveToFav(product: Product) {
        lifecycleScope.launch {
            viewModel.addProductToFavorites(product)
            viewModel.addedToFav.collect {
                if (it == "Done") Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT)
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