package com.azrosk.sell_it.users.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.ProductItemBinding
import com.bumptech.glide.Glide

class ProductsRvAdapter(
    private val productList: List<Product>,
    private val itemListener: (product: Product) -> Unit,
    private val favoriteListener: (product: Product) -> Unit
) : RecyclerView.Adapter<ProductsRvAdapter.ProductsViewHolder>() {

    class ProductsViewHolder(
        itemListener: (product: Product) -> Unit,
        favoriteListener: (product: Product) -> Unit,
        val binding: ProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var product: Product? = null
        fun bind(curProduct: Product) {
            val priceString = String.format("%.2f", curProduct.price)
            val priceTag = "$priceString руб"
            binding.textViewProductName.text = curProduct.name
            binding.textViewProductPrice.text = priceTag
            Glide.with(binding.root)
                .load(curProduct.imagesUrl)
                .error(R.drawable.sell_it_logo)
                .into(binding.imageViewProductImage)
            product = curProduct
        }

        init {
            binding.root.setOnClickListener { itemListener(product!!) }
            binding.buttonAddProdToFav.setOnClickListener { favoriteListener(product!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            itemListener, favoriteListener, ProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(productList[position])
    }


}
