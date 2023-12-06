package com.azrosk.sell_it.admin.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azrosk.data.model.FeedBack
import com.azrosk.data.repository.FeedBackRepository
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedBackRepository
) : ViewModel() {

    private val _feedback = MutableStateFlow<ScreenState<List<FeedBack>?>>(ScreenState.Loading())
    val feedback = _feedback

    init {
        getFeedback()
    }

    private fun getFeedback() = viewModelScope.launch {
        try {
            feedbackRepository.getFeedBacks().let {
                if (it.isEmpty()) _feedback.value = ScreenState.Error("No FeedBacks")
                else _feedback.value = ScreenState.Success(it)
            }
        } catch (e: Exception) {
            _feedback.value = ScreenState.Error(e.message.toString())
        }
    }

}