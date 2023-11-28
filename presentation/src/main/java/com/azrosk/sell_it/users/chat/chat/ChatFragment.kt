package com.azrosk.sell_it.users.chat.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.MessageItem
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentChatBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val binding by viewBinding(FragmentChatBinding::bind)
    private val viewModel: ChatViewModel by viewModels()
    private var msgAdapter: MessageAdapter? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = arguments?.getString("chat_name") ?: ""
        val receiverUid = arguments?.getString("chat_uid") ?: ""
        val imageUrl = arguments?.getString("chat_image") ?: ""
        val senderUid = firebaseAuth.currentUser?.uid ?: ""

        binding.textViewProfileName.text = name
        Glide.with(binding.root)
            .load(Uri.parse(imageUrl))
            .error(R.drawable.turquoise_profile_icon)
            .into(binding.chatProfileImg)

        setChatDetails(receiverUid, senderUid)


    }

    private fun setRecyclerView(messageList: List<MessageItem>) {
        Log.d("MsgList", messageList.size.toString())
        messageList.sortedBy { it.timestamp }
        msgAdapter = MessageAdapter(messageList)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.scrollToPosition(messageList.size - 1)
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.adapter = msgAdapter
    }

    private fun setChatDetails(receiverUid: String, senderUid: String) {
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid
        displayMsg(senderRoom, receiverRoom)
        binding.btnSendMsg.setOnClickListener {
            sendMsg(senderUid, receiverUid)
            displayMsg(senderRoom, receiverRoom)
            binding.etSendMsg.setText("")

        }
    }

    private fun sendMsg(senderUid: String, receiverUid: String) {
        val message = binding.etSendMsg.text.toString()
        val msgObject = MessageItem(message, senderUid, Constants.getCurrentTimestamp())
        lifecycleScope.launch {
            viewModel.sendMessage(receiverUid, senderUid, msgObject)
        }
    }

    private fun displayMsg(senderRoom: String, receiverUid: String) {
        lifecycleScope.launch {
            viewModel.getMsgList(senderRoom, receiverUid)
            viewModel.msgList.collect { state ->

                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {}

                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) setRecyclerView(state.data)
                    }

                    else -> {}
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        msgAdapter = null
    }

}