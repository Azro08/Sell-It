package com.azrosk.sell_it.users.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Category
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.CategoryItemBinding
import com.bumptech.glide.Glide

class CategoryRvAdapter(
    private val categoryList: List<Category>,
    private val listener: (category: Category) -> Unit
) : RecyclerView.Adapter<CategoryRvAdapter.CategoryViewHolder>() {

    private var lastClickedIndex: Int = -1

    class CategoryViewHolder(
        private var adapter: CategoryRvAdapter,
        listener: (category: Category) -> Unit,
        private val binding: CategoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var category: Category? = null
        fun bind(curCategory: Category, position: Int) {
            binding.textViewProductCategory.text = curCategory.name
            Glide.with(binding.root).load(curCategory.icon)
                .error(R.drawable.image_container_background)
                .into(binding.imageProductCategory)
            category = curCategory

            if (adapter.lastClickedIndex == position) {
                binding.textViewProductCategory.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.pink
                    )
                )
            } else {
                binding.textViewProductCategory.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.black
                    )
                )
            }
        }

        init {
            binding.root.setOnClickListener {
                listener(category!!)
                // Update the background for the last clicked item
                if (adapter.lastClickedIndex != -1) {
                    adapter.notifyItemChanged(adapter.lastClickedIndex)
                }

                // Update the background for the currently clicked item
                adapter.lastClickedIndex = adapterPosition
                adapter.notifyItemChanged(adapterPosition)

                // Notify the listener
                adapter.listener(category!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            this,
            listener,
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

}