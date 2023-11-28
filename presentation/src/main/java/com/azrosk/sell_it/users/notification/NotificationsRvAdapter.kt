package com.azrosk.sell_it.users.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Order
import com.azrosk.sell_it.databinding.NotificationItemBinding

class NotificationsRvAdapter (
    private val orderList : List<Order>,
    private val listener: (order: Order) -> Unit
) : RecyclerView.Adapter<NotificationsRvAdapter.NotificationViewHolder>(){

    class NotificationViewHolder(listener : (order : Order) -> Unit, private val binding : NotificationItemBinding):
        RecyclerView.ViewHolder(binding.root){
        private var order : Order? = null
        fun bind(curOrder : Order){
            binding.textViewProductName.text = curOrder.productName
            binding.textViewOrderedBy.text = curOrder.orderedBy
            binding.textViewOrderStatus.text = curOrder.status
            binding.textViewOrderDate.text = curOrder.date
            order = curOrder
        }
        init {
            binding.root.setOnClickListener { listener(order!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(listener, NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

}