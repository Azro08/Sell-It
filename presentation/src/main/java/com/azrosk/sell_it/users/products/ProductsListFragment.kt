package com.azrosk.sell_it.users.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentProductsListBinding

class ProductsListFragment : Fragment(R.layout.fragment_products_list) {
    private val binding by viewBinding(FragmentProductsListBinding::bind)
}