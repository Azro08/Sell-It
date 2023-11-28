package com.azrosk.sell_it.users.chat.chat_list

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.ChatsListItem
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.ChatsLlistTicketBinding
import com.bumptech.glide.Glide

class ChatsListAdapter(
    private val chatsListItem: List<ChatsListItem>,
    private val listener: (myChat: ChatsListItem) -> Unit
) : RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder>() {

    class ChatViewHolder(
        listener: (myChat: ChatsListItem) -> Unit,
        private val binding: ChatsLlistTicketBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var chatsListItem: ChatsListItem? = null
        fun bind(currentChatsList: ChatsListItem) {
            binding.textViewChatUserName.text = currentChatsList.fullName
            binding.textViewMsgTime.text = currentChatsList.time
            binding.textViewChatLastMsg.text = currentChatsList.lastMsg
            Glide.with(binding.root)
                .load(Uri.parse(currentChatsList.imageUrl))
                .error(R.drawable.profile_icon)
                .into(binding.imageViewChatImage)
            chatsListItem = currentChatsList
        }

        init {
            binding.root.setOnClickListener { listener(chatsListItem!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            listener,
            ChatsLlistTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatsListItem.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatsListItem[position])
    }
}