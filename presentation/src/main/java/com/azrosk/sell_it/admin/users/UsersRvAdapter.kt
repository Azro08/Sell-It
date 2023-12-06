package com.azrosk.sell_it.admin.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.Users
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.UserItemBinding
import com.bumptech.glide.Glide

class UsersRvAdapter(
    private var usersList: List<Users>,
    private val listener: (account: Users) -> Unit
) : RecyclerView.Adapter<UsersRvAdapter.AccountViewHolder>() {

    class AccountViewHolder(
        listener: (account: Users) -> Unit,
        private var binding: UserItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var user: Users? = null
        fun bind(curUser: Users) {

            binding.textViewName.text = curUser.fullName
            binding.textViewEmail.text = curUser.email
            try {
                Glide.with(binding.root).load(curUser.imageUrl)
                    .error(R.drawable.turquoise_profile_icon)
                    .into(binding.profileImage)
            } catch (e: Exception) {
                binding.profileImage.setBackgroundResource(R.drawable.turquoise_profile_icon)
            }
            user = curUser
        }

        init {
            binding.imageButtonDeleteUser.setOnClickListener { listener(user!!) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsersList(newUsersList: List<Users>) {
        usersList = newUsersList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            listener,
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

}