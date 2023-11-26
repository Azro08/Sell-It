package com.azrosk.sell_it.users.product_details

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.azrosk.sell_it.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ProductDetailsImagesPagerAdapter(
    private val context: Context,
    private val imageUris: List<String>,
) :
    PagerAdapter() {

    override fun getCount(): Int {
        return imageUris.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.item_product_image, container, false)

        val imageView = layout.findViewById<ImageView>(R.id.imageViewItemProductImage)

        // You can use Picasso, Glide, or any other image loading library here
        // Using Glide for example:
        Glide.with(context)
            .load(Uri.parse(imageUris[position]))
            .error(R.drawable.sell_it_logo)
            .apply(RequestOptions().centerCrop())
            .into(imageView)

        container.addView(layout)
        return layout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
