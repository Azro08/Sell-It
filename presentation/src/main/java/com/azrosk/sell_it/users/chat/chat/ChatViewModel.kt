package com.azrosk.sell_it.users.chat.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.MessageItem
import com.azrosk.data.repository.ChatRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _msgList = MutableStateFlow<ScreenState<List<MessageItem>?>>(ScreenState.Loading())
    val msgList = _msgList

    fun sendMessage(receiverUid: String, senderUid: String, msgObject: MessageItem) = viewModelScope.launch{
        chatRepository.sendMessage(receiverUid, senderUid, msgObject)
    }

    fun getMsgList(senderRoom: String, receiverRoom: String) = viewModelScope.launch {
        try {
            chatRepository.displayMsg(senderRoom, receiverRoom).let {
                Log.d("viemodel", it.toString())
                if (it.isNotEmpty()) _msgList.value = ScreenState.Success(it)
                else _msgList.value = ScreenState.Error("No messages")
            }
        } catch (e: Exception) {
            _msgList.value = ScreenState.Error(e.message.toString())
        }
    }

}