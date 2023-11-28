package com.azrosk.sell_it.users.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.FeedBack
import com.azrosk.data.repository.FeedBackRepository
import com.azrosk.domain.usecase.DeleteAccountUseCase
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val feedBackRepository: FeedBackRepository
) : ViewModel() {

    private val _accountDeleted = MutableStateFlow("")
    val accountDeleted = _accountDeleted

    private val _feedBackSent = MutableStateFlow<ScreenState<String?>>(ScreenState.Loading())
    val feedBackSent = _feedBackSent

    fun deleteAccount(uid: String) = viewModelScope.launch {

        deleteAccountUseCase.invoke(uid).let {
            _accountDeleted.value = it
        }

    }

    fun sendFeedBack(feedBack: FeedBack) = viewModelScope.launch {
        feedBackRepository.sendFeedback(feedBack).let {
            if (it == "Done") _feedBackSent.value = ScreenState.Success(it)
            else _feedBackSent.value = ScreenState.Error(it)
        }
    }


}