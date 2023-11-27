package com.azrosk.sell_it.users.notification

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
import com.azrosk.data.model.Order
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentNotificationsBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private val binding by viewBinding(FragmentNotificationsBinding::bind)
    private val viewModel: NotificationViewModel by viewModels()
    private var rvAdapter: NotificationsRvAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getOrders()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getOrders() {
        lifecycleScope.launch {

            viewModel.orders.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) displayOrders(state.data)
                        else Toast.makeText(context, getString(R.string.no_orders), Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun displayOrders(orders: List<Order>) {
        rvAdapter = NotificationsRvAdapter(orders) {
            findNavController().navigate(
                R.id.nav_notification_order_details,
                bundleOf(Pair(Constants.ORDER_ID, it.id))
            )
        }
        binding.rvNotifications.setHasFixedSize(true)
        binding.rvNotifications.layoutManager = LinearLayoutManager(context)
        binding.rvNotifications.adapter = rvAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        rvAdapter = null
    }

}