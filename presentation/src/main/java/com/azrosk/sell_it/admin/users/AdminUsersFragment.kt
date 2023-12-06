package com.azrosk.sell_it.admin.users

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.data.model.Users
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentAdminUsersBinding
import com.azrosk.sell_it.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminUsersFragment : Fragment(R.layout.fragment_admin_users) {
    private val binding by viewBinding(FragmentAdminUsersBinding::bind)
    private var rvAdapter: UsersRvAdapter? = null
    private val viewModel: UsersViewModel by viewModels()
    private var usersList = arrayListOf<Users>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModelOutputs()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        search()
    }

    private fun search() {
        binding.editTextSearchStudent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                performSearch(searchText)
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.filterUsersList(query)
        rvAdapter?.updateUsersList(filteredList)
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResumed", "onResume")
        viewModel.refresh()
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.users.collect { state ->
                Log.d("AccountsListFragment", "accounts: ${state.data?.size}")
                when (state) {
                    is ScreenState.Loading -> {}

                    is ScreenState.Success -> {
                        binding.loadingGif.visibility = View.GONE
                        binding.textViewError.visibility = View.GONE
                        binding.rvUsersList.visibility = View.VISIBLE
                        if (!state.data.isNullOrEmpty()) {
                            displayUsers(state.data)
                        } else handleError("No Users found")
                    }

                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }
                }
            }
        }
    }

    private fun displayUsers(users: List<Users>) {
        Log.d("display entered", users.size.toString())
        usersList.clear()
        usersList.addAll(users)
        rvAdapter = UsersRvAdapter(usersList) {
            showConfirmationDialog(it)
        }
        binding.rvUsersList.setHasFixedSize(true)
        binding.rvUsersList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsersList.adapter = rvAdapter
    }

    private fun showConfirmationDialog(user: Users) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete User?")
        builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
            deleteUser(user)
        }
        builder.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private fun deleteUser(user: Users) {
        lifecycleScope.launch {
            viewModel.deleteUser(user.id)
            viewModel.userDeleted.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        usersList.remove(user)
                        Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }
                }
            }
        }
    }

    private fun handleError(errorMsg: String) {
        binding.rvUsersList.visibility = View.GONE
        binding.loadingGif.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = errorMsg
    }

    override fun onDestroy() {
        super.onDestroy()
        rvAdapter = null
    }

}
