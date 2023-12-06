package com.azrosk.sell_it.admin.feedback

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.FeedBack
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentAdminFeedbackBinding
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AdminFeedbackFragment : Fragment(R.layout.fragment_admin_feedback) {
    private val binding by viewBinding(FragmentAdminFeedbackBinding::bind)
    private val viewModel: AdminFeedbackViewModel by viewModels()
    private var rvFeedBackAdapter: FeedbackRvAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFeedBack()
    }

    private fun getFeedBack() {
        lifecycleScope.launch {
            viewModel.feedback.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message.toString())
                    }

                    is ScreenState.Success -> {
                        if (state.data.isNullOrEmpty()) handleError(state.message.toString())
                        else displayFeedBack(state.data)
                    }
                }
            }
        }
    }

    private fun displayFeedBack(feedBacks: List<FeedBack>) {
        binding.rvAdminFeedbacks.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        rvFeedBackAdapter = FeedbackRvAdapter(feedBacks)
        binding.rvAdminFeedbacks.setHasFixedSize(true)
        binding.rvAdminFeedbacks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminFeedbacks.adapter = rvFeedBackAdapter
    }

    private fun handleError(message: String) {
        binding.rvAdminFeedbacks.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        rvFeedBackAdapter = null
    }

}