package com.azrosk.sell_it.users.my_products

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Product
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.MyProductItemBinding
import com.bumptech.glide.Glide

class MyProductsRvAdapter(
    private var productList: List<Product>,
    private val itemListener: (product: Product) -> Unit,
    private val deleteListener: (product: Product) -> Unit
) : RecyclerView.Adapter<MyProductsRvAdapter.ProductsViewHolder>() {

    class ProductsViewHolder(
        itemListener: (product: Product) -> Unit,
        favoriteListener: (product: Product) -> Unit,
        val binding: MyProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var product: Product? = null
        fun bind(curProduct: Product) {
            val priceString = String.format("%.2f", curProduct.price)
            val priceTag = "$priceString руб"
            binding.textViewProductName.text = curProduct.name
            binding.textViewProductPrice.text = priceTag
            Glide.with(binding.root)
                .load(Uri.parse(curProduct.imagesUrl[0]))
                .error(R.drawable.sell_it_logo)
                .into(binding.imageViewProductImage)
            product = curProduct
        }

        init {
            binding.root.setOnClickListener { itemListener(product!!) }
            binding.buttonDeleteMyProduct.setOnClickListener { favoriteListener(product!!) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFoodList(newProductList: List<Product>) {
        productList = newProductList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            itemListener, deleteListener, MyProductItemBinding.inflate(
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
