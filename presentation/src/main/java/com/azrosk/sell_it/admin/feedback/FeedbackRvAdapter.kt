package com.azrosk.sell_it.admin.feedback

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.FeedBack
import com.azrosk.sell_it.databinding.FeedbackItemBinding

class FeedbackRvAdapter(private val feedBacks: List<FeedBack>) :
    RecyclerView.Adapter<FeedbackRvAdapter.FeedBackViewHolder>() {

    class FeedBackViewHolder(private val binding: FeedbackItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var feedback: FeedBack? = null
        fun bind(curFeedback: FeedBack) {
            binding.textViewFeedback.text = curFeedback.feedBack
            binding.textViewSentBy.text = curFeedback.userEmail
            binding.textViewFeedbackDate.text = curFeedback.date
            feedback = curFeedback
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedBackViewHolder {
        return FeedBackViewHolder(
            FeedbackItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return feedBacks.size
    }

    override fun onBindViewHolder(holder: FeedBackViewHolder, position: Int) {
        holder.bind(feedBacks[position])
    }


}