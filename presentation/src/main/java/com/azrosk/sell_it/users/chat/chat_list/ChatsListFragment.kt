package com.azrosk.sell_it.users.chat.chat_list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.ChatsListItem
import com.azrosk.data.model.Users
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentChatsListBinding
import com.azrosk.sell_it.util.Constants
import com.azrosk.sell_it.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatsListFragment : Fragment(R.layout.fragment_chats_list) {
    private val binding by viewBinding(FragmentChatsListBinding::bind)
    private var rvAdapter: ChatsListAdapter? = null
    private val viewModel: ChatListViewModel by viewModels()
    private val chatsList = mutableListOf<ChatsListItem>()
    @Inject lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getUsers()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getUsers() {
        lifecycleScope.launch {
            viewModel.users.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        binding.rvChatList.visibility = View.GONE
                        binding.textViewError.visibility = View.VISIBLE
                    }

                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) setUpChatsList(state.data)
                        else {
                            binding.rvChatList.visibility = View.GONE
                            binding.textViewError.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    private fun setUpChatsList(users: List<Users>) {
        binding.rvChatList.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        val uid = firebaseAuth.currentUser?.uid ?: ""
        chatsList.clear()
        for (user in users) {
            if (user.id != uid ) chatsList.add(ChatsListItem(user.id, user.fullName, user.imageUrl))
        }
        rvAdapter = ChatsListAdapter(chatsList) {
            val namePair = Pair(Constants.CHAT_NAME, it.fullName)
            val idPair = Pair(Constants.CHAT_UID, it.userId)
            val imgPair = Pair(Constants.CHAT_IMAGE, it.imageUrl)
            val bundle = bundleOf(namePair, idPair, imgPair)
            findNavController().navigate(R.id.nav_chat_list_to_chat, bundle)
        }
        binding.rvChatList.setHasFixedSize(true)
        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatList.adapter = rvAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        rvAdapter = null
    }

}