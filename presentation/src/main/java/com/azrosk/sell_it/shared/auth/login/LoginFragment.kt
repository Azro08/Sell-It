package com.azrosk.sell_it.shared.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.sell_it.util.AuthManager
import com.azrosk.sell_it.users.MainActivity
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentLoginBinding
import com.azrosk.sell_it.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels()
    @Inject lateinit var authManager: AuthManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonLogin.setOnClickListener {
            if (allFieldsAreFilled()) login()
            else Toast.makeText(requireContext(), getString(R.string.fill_upFields), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun login() {
        binding.buttonLogin.isClickable = false
        lifecycleScope.launch {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            viewModel.login(email, password)
            viewModel.loggedIn.collect { result ->
                if (result == Constants.ADMIN || result == Constants.USER) {
                    authManager.saveUer(email)
                    authManager.saveRole(result)
                    navToMainActivity()
                }
                else {
                    if (result != "") Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                    binding.buttonLogin.isClickable = true
                }
            }
        }
    }

    private fun navToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun allFieldsAreFilled(): Boolean {
        return !(binding.editTextEmail.text.toString()
            .isEmpty() || binding.editTextPassword.text.toString().isEmpty())
    }

}