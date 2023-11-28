package com.azrosk.sell_it.users.chat.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Users
import com.azrosk.data.repository.ChatRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel(){

    private val _users = MutableStateFlow<ScreenState<List<Users>?>>(ScreenState.Loading())
    val users = _users

    init {
        getUsers()
    }

    fun refresh(){
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch{
        chatRepository.getUsers().let{
            if (it.isNotEmpty()) _users.value = ScreenState.Success(it)
            else _users.value = ScreenState.Error("No users found")
        }
    }

}