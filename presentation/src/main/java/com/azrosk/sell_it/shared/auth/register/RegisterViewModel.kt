package com.azrosk.sell_it.shared.auth.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.repository.UsersRepository
import com.azrosk.domain.model.User
import com.azrosk.domain.usecase.RegisterUseCase
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _imageUploaded = MutableStateFlow<ScreenState<Uri?>>(ScreenState.Loading())
    val imageUploaded: MutableStateFlow<ScreenState<Uri?>> = _imageUploaded

    private val _registerState = MutableStateFlow("")
    val registerState = _registerState

    fun uploadImageAndGetUri(userId: String, imageUri: Uri) = viewModelScope.launch {
        try {
            usersRepository.uploadImageAndGetUri(userId, imageUri).let {
                if (it != null) _imageUploaded.value = ScreenState.Success(it)
                else _imageUploaded.value = ScreenState.Error("Error Loading image")
            }
        } catch (e: Exception) {
            _imageUploaded.value = ScreenState.Error(e.message.toString())
        }
    }

    fun register(user: User, password: String) = viewModelScope.launch {
        registerUseCase.invoke(user, password).let {
            _registerState.value = it
        }
    }

}