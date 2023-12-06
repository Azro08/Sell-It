package com.azrosk.sell_it.admin.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.Users
import com.azrosk.data.repository.UsersRepositoryImpl
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.FirebaseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val usersRepository: UsersRepositoryImpl) :
    ViewModel() {

    private val _users = MutableStateFlow<ScreenState<List<Users>?>>(ScreenState.Loading())
    val users: MutableStateFlow<ScreenState<List<Users>?>> = _users

    private val _userDeleted = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val userDeleted: MutableStateFlow<ScreenState<String?>> = _userDeleted

    init {
        getUsers()
    }

    fun refresh() {
        getUsers()
    }

    fun filterUsersList(query: String): List<Users> {
        return when (val currentState = _users.value) {
            is ScreenState.Success -> {
                currentState.data?.filter { user ->
                    user.fullName.contains(query, ignoreCase = true)
                } ?: emptyList()
            }

            else -> emptyList()
        }
    }

    private fun getUsers() = viewModelScope.launch {
        _users.value = ScreenState.Loading()
        try {
            usersRepository.getUsers().let {
                try {
                    _users.value = ScreenState.Success(it)
                } catch (e: FirebaseException) {
                    _users.value = ScreenState.Error(e.message.toString())
                }
            }
        } catch (e: UnknownHostException) {
            _users.value = ScreenState.Error(e.message.toString())
        }
    }

    fun deleteUser(uid: String) = viewModelScope.launch {
        try {
            usersRepository.deleteUser(uid).let {
                if (it == "Done") {
                    usersRepository.deleteAccount(uid).let { response ->
                        if (response == "Done") _userDeleted.value = ScreenState.Success(response)
                        else _userDeleted.value = ScreenState.Error(response)
                    }
                    getUsers()
                } else {
                    _userDeleted.value = ScreenState.Error(it)
                }
            }
        } catch (e: HttpException) {
            Log.d("AccountListViewModel", e.message().toString())
            _userDeleted.value = ScreenState.Error(e.message())
        }
    }


}