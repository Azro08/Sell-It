package com.azrosk.sell_it.users.favorite

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentFavoritesBinding
import com.azrosk.sell_it.users.products.ProductsRvAdapter
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private val viewModel: FavoritesViewModel by viewModels()
    private var rvAdapter: ProductsRvAdapter? = null
    private val productsList = mutableListOf<Product>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFavoriteList()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getFavoriteList() {
        lifecycleScope.launch {
            viewModel.favorites.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        Log.d("FavRespone", state.data.toString())
                        if (!state.data.isNullOrEmpty()) displayFavoriteList(state.data)
                        else handleError("No products saved!")
                    }

                }
            }
        }
    }

    private fun displayFavoriteList(productListResponse: List<Product?>) {
        Log.d("ProductsRespone", productListResponse.toString())
        binding.textViewError.visibility = View.GONE
        binding.rvFavorites.visibility = View.VISIBLE
        productsList.clear()
        for (product in productListResponse) {
            if (product != null) productsList.add(product)
        }
        rvAdapter = ProductsRvAdapter(productsList, { navToDetails(it.id) }, { removeFromFav(it.id) })
        binding.rvFavorites.layoutManager = GridLayoutManager(context, 2)
        binding.rvFavorites.setHasFixedSize(true)
        binding.rvFavorites.adapter = rvAdapter

    }

    private fun removeFromFav(id: String) {
        lifecycleScope.launch {
            viewModel.removeFavorite(id)
        }
    }

    private fun navToDetails(id: String) {
        findNavController().navigate(R.id.nav_fav_details, bundleOf(Pair(Constants.PRODUCT_ID, id)))
    }

    private fun handleError(msg: String) {
        binding.rvFavorites.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = msg
    }

    override fun onDestroy() {
        super.onDestroy()
        rvAdapter = null
    }

}