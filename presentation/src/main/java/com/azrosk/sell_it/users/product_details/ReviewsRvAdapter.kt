package com.azrosk.sell_it.users.product_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Review
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.ReviewItemBinding
import com.bumptech.glide.Glide

class ReviewsRvAdapter (private val reviews : List<Review>) : RecyclerView.Adapter<ReviewsRvAdapter.ReviewViewHolder>(){

    class ReviewViewHolder (private val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var review: Review? = null
        fun bind(curReview: Review) {
            binding.textViewReviewText.text = curReview.review
            binding.textViewReviewDate.text = curReview.date
            Glide.with(binding.root)
                .load(curReview.userIcon)
                .error(R.drawable.profile_icon)
                .into(binding.imageViewReviewOwnerIcon)
            review = curReview

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

}