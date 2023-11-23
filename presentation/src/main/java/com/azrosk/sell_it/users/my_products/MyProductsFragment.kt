package com.azrosk.sell_it.users.my_products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentMyProductsBinding

class MyProductsFragment : Fragment(R.layout.fragment_my_products) {
    private val binding by viewBinding(FragmentMyProductsBinding::bind)
}