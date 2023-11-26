package com.azrosk.sell_it.shared.auth.register

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.azrosk.domain.model.User
import com.azrosk.sell_it.R
import com.azrosk.sell_it.util.ScreenState
import com.azrosk.sell_it.databinding.FragmentRegisterBinding
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri = Uri.parse("")
    private var imageUrl = ""
    @Inject lateinit var firebaseAuth : FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setMediaPicker()
    }

    private fun setMediaPicker() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                Glide.with(binding.root).load(uri)
                    .error(R.drawable.profile_icon)
                    .into(binding.imageViewProfileIcon)
                imageUri = uri
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSignup.setOnClickListener {
            if (allFieldsAreFilled()) {
                if (imageUri != Uri.parse("")) uploadImage()
                else register()
            }
            else Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.imageViewProfileIcon.setOnClickListener{
            setProfileImage()
        }
    }

    private fun setProfileImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadImage() {
        lifecycleScope.launch {
            Log.d("ImageUri", imageUri.toString())
            val uid = firebaseAuth.currentUser?.uid ?: ""
            viewModel.uploadImageAndGetUri(uid, imageUri)
            viewModel.imageUploaded.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        imageUrl = state.data.toString()
                        register()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun register() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val rePassword = binding.editTextRePassword.text.toString()
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextRePhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()
        if (password == rePassword) {
            val newUser = User(
                email = email,
                address = address,
                phoneNumber = phoneNumber,
                role = "user",
                fullName = fullName,
                imageUrl = imageUrl
                )
            lifecycleScope.launch {
                binding.buttonSignup.visibility = View.GONE
                viewModel.register(newUser, password)
                viewModel.registerState.collect{
                    if (it == "Done") {
                        Toast.makeText(requireContext(), "Registration is done!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireActivity(), AuthActivity::class.java))
                        requireActivity().finish()
                    } else {
                        binding.buttonSignup.visibility = View.VISIBLE
                        if (it != "") Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        Log.d("Register", it)
                    }
                }
            }

        } else Toast.makeText(requireContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show()
    }

    private fun allFieldsAreFilled(): Boolean {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val rePassword = binding.editTextRePassword.text.toString()
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextRePhoneNumber.text.toString()
        val fullName = binding.editTextFullName.text.toString()

        return !(email.isEmpty() || password.isEmpty() || rePassword.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || fullName.isEmpty())

    }

}