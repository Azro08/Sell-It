package com.azrosk.sell_it.users.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentSettingsBinding
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.azrosk.sell_it.shared.language.LanguageFragment
import com.azrosk.sell_it.users.feedback.SendFeedBackFragment
import com.azrosk.sell_it.util.AuthManager
import com.azrosk.sell_it.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var authManager: AuthManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        val email = firebaseAuth.currentUser?.email ?: ""
        val bundle = bundleOf(Pair(Constants.USER_ID, uid), Pair(Constants.EMAIL, email))
        binding.textViewDelAccount.setOnClickListener {
            showConfirmationDialog()
        }
        binding.textViewLanguage.setOnClickListener {
            val languageFragment = LanguageFragment()
            languageFragment.show(parentFragmentManager, "languageFragment")

        }
        binding.textViewFeedback.setOnClickListener {
            val feedbackFragment = SendFeedBackFragment()
            feedbackFragment.arguments = bundle
            feedbackFragment.show(parentFragmentManager, "sendFeedBackFragment")
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_account))
        builder.setPositiveButton(getString(R.string.yes)) { _: DialogInterface?, _: Int ->
            deleteAccount()
        }
        builder.setNegativeButton(
            getString(R.string.no)
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.show()
    }

    private fun deleteAccount() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        lifecycleScope.launch {
            viewModel.deleteAccount(uid)
            viewModel.accountDeleted.collect {
                if (it == "Done") logout()
                else Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        authManager.removeUser()
        authManager.removeRole()
        startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
    }

}