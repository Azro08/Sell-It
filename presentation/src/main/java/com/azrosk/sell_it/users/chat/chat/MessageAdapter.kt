package com.azrosk.sell_it.users.chat.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azrosk.data.model.MessageItem
import com.azrosk.sell_it.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageItemList: List<MessageItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val itemSent = 1
    private val itemRec = 2

    class SenderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val sendMsg : TextView = itemView.findViewById(R.id.tvSentMsg)
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recMsg : TextView = itemView.findViewById(R.id.tvRecMsg)
    }

    override fun getItemViewType(position: Int): Int {
        val curMessage = messageItemList[position]

        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(curMessage.senderId)){
            itemSent
        } else{
            itemRec
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_msg_ticket, parent, false)
            SenderViewHolder(view)
        } else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_msg_ticket, parent, false)
            ReceiverViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curMessage = messageItemList[position]

        if(holder.javaClass == SenderViewHolder::class.java){
            val viewHolder = holder as SenderViewHolder
            viewHolder.sendMsg.text = curMessage.message
        }
        else if (holder.javaClass == ReceiverViewHolder::class.java){
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.recMsg.text = curMessage.message
        }
    }

    override fun getItemCount(): Int {
        return messageItemList.size
    }
}