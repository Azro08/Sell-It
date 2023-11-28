package com.azrosk.sell_it.users.products

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.azrosk.sell_it.util.AuthManager
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductsListFragment : Fragment(R.layout.fragment_products_list) {
    private val binding by viewBinding(FragmentProductsListBinding::bind)
    private val viewModel: ProductsListViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private var productsRvAdapter: ProductsRvAdapter? = null
    private var categoryRvAdapter: CategoryRvAdapter? = null
    private var category: String = "Все"
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var authManager: AuthManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavDrawer()
        getCategories()
        getProducts()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(category)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        search()
    }

    private fun setNavDrawer() {
        setNavDrawerHeader()
        setMenu()
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.buttonNavDrawer.setOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navViewDrawer.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.notificationsFragment -> findNavController().navigate(R.id.notificationsFragment)
                R.id.chatsListFragment -> findNavController().navigate(R.id.chatsListFragment)
                R.id.myProductsFragment -> findNavController().navigate(R.id.myProductsFragment)
                R.id.itemLogout -> logout()
            }

            true
        }

    }

    private fun setNavDrawerHeader() {
        lifecycleScope.launch {
            val uid = firebaseAuth.currentUser?.uid ?: ""
            viewModel.getUser(uid)
            viewModel.user.collect {
                if (it != null) {
                    val name = it.fullName
                    val email = it.email
                    val image = it.imageUrl

                    val headerView = binding.navViewDrawer.getHeaderView(0)
                    val tvAccountName: TextView = headerView.findViewById(R.id.headerTvName)
                    val tvAccountEmail: TextView = headerView.findViewById(R.id.headerTvEmail)
                    val accountIv: ImageView = headerView.findViewById(R.id.headerIvAccount)

                    tvAccountEmail.text = email
                    tvAccountName.text = name
                    Glide.with(this@ProductsListFragment)
                        .load(image)
                        .error(R.drawable.turquoise_profile_icon)
                        .into(accountIv)

                }
            }
        }
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.drawer_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return toggle.onOptionsItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun logout() {
        firebaseAuth.signOut()
        authManager.removeUser()
        startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
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
        productsRvAdapter?.updateFoodList(filteredList)
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
                        else handleError(getString(R.string.no_products_found))
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
        findNavController().navigate(
            R.id.nav_list_to_prod_details,
            bundleOf(Pair(Constants.PRODUCT_ID, product.id))
        )
    }

    private fun saveToFav(product: Product) {
        lifecycleScope.launch {
            viewModel.addProductToFavorites(product)
            viewModel.addedToFav.collect {
                if (it == "Done") Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
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