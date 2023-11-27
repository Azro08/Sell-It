package com.azrosk.sell_it.users.feedback

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.azrosk.data.model.FeedBack
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentSendFeedBackBinding
import com.azrosk.sell_it.users.settings.SettingsViewModel
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendFeedBackFragment : DialogFragment() {
    private var _binding: FragmentSendFeedBackBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentSendFeedBackBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }
        val uid = arguments?.getString(Constants.USER_ID) ?: ""
        val email = arguments?.getString(Constants.EMAIL) ?: ""
        binding.buttonSendFeedback.setOnClickListener {
            if (binding.editTextFeedBack.text.toString().isNotEmpty()) sendFeedback(uid, email)
        }
        return builder.create()
    }

    private fun sendFeedback(uid: String, email: String) {
        val feedBack = binding.editTextFeedBack.text.toString()
        val date = Constants.getCurrentDate()
        val feedback = FeedBack(Constants.generateRandomId(), uid, email, feedBack, date)

        lifecycleScope.launch {
            viewModel.sendFeedBack(feedback)
            viewModel.feedBackSent.collect { state ->

                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ScreenState.Success -> {
                        Toast.makeText(requireContext(), getString(R.string.sent), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }

                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}