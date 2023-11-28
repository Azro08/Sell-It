package com.azrosk.sell_it.shared.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _loggedIn = MutableStateFlow("")
    val loggedIn = _loggedIn

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            loginUseCase.invoke(email, password).let {
                _loggedIn.value = it
            }
        } catch (e: Exception) {
            _loggedIn.value = e.message.toString()
            Log.d("ResultExcVM", e.message.toString())
        }
    }

}