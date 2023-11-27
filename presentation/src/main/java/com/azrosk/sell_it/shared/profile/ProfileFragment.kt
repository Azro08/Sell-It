package com.azrosk.sell_it.shared.profile

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
import com.azrosk.data.model.Users
import com.azrosk.sell_it.R
import com.azrosk.sell_it.databinding.FragmentProfileBinding
import com.azrosk.sell_it.shared.auth.AuthActivity
import com.azrosk.sell_it.util.AuthManager
import com.azrosk.sell_it.util.ScreenState
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()
    private var userId = ""
    @Inject lateinit var authManager: AuthManager
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri = Uri.parse("")
    private var imageUrl = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        setMediaPicker()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (firebaseAuth.currentUser?.uid.isNullOrEmpty()) logout()
        else userId = firebaseAuth.currentUser?.uid!!
        binding.profileImage.setOnClickListener {
            setProfileImage()
        }
        binding.buttonEditProfile.setOnClickListener {
            editProfile()
        }
        binding.textViewProfileEmail.text = authManager.getUser()
        viewModelOutputs()
    }

    private fun setProfileImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setMediaPicker() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                Glide.with(binding.root).load(uri)
                    .error(R.drawable.profile_icon)
                    .into(binding.profileImage)
                imageUri = uri
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(uri, flag)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun viewModelOutputs() {
        lifecycleScope.launch {
            viewModel.getUser(userId)
            viewModel.users.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        if (state.data != null) displayProfileDetails(state.data)
                        else Toast.makeText(requireContext(), getString(R.string.user_not_found), Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun displayProfileDetails(user: Users) {
        binding.editTextAddress.setText(user.address)
        binding.editTextPhoneNum.setText(user.phoneNumber)
        Glide.with(binding.root).load(user.imageUrl)
            .error(R.drawable.profile_icon)
            .into(binding.profileImage)
    }

    private fun areAllFieldsFilled(): Boolean {
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextPhoneNum.text.toString()

        return !(address.isEmpty() || phoneNumber.isEmpty())

    }

    private fun editProfile() {
        binding.buttonEditProfile.visibility = View.GONE
        binding.buttonSaveProfile.visibility = View.VISIBLE
        binding.buttonSaveProfile.visibility = View.VISIBLE
        binding.editTextAddress.visibility = View.VISIBLE
        binding.editTextPhoneNum.visibility = View.VISIBLE
        binding.editTextPassword.visibility = View.VISIBLE
        binding.editTextOldPassword.visibility = View.VISIBLE
        binding.profileImage.isClickable = true

        binding.buttonSaveProfile.setOnClickListener {
            if (areAllFieldsFilled()) {
                if (imageUri != Uri.parse("")) uploadImage()
                else saveUser()
            } else Toast.makeText(requireContext(), getString(R.string.fill_upFields), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun uploadImage() {
        lifecycleScope.launch {
            Log.d("ImageUri", imageUri.toString())
            viewModel.uploadImageAndGetUri(firebaseAuth.currentUser?.uid!!, imageUri)
            viewModel.imageUploaded.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        imageUrl = state.data.toString()
                        saveUser()
                    }

                    is ScreenState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUser() {
        binding.buttonSaveProfile.visibility = View.GONE
        binding.saveLoadingGif.visibility = View.VISIBLE
        val address = binding.editTextAddress.text.toString()
        val phoneNumber = binding.editTextPhoneNum.text.toString()
        var password = binding.editTextPassword.text.toString()
        var oldPassword = ""
        var email = ""
        if (password.isEmpty()) password = ""
        else {
            if (binding.editTextOldPassword.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.old_password_is_required), Toast.LENGTH_SHORT)
                    .show()
            } else {
                oldPassword = binding.editTextOldPassword.text.toString()
                email = authManager.getUser()
            }
        }

        val updatedFields: Map<String, Any> = if (imageUri != Uri.parse("")) {
            mapOf(
                "address" to address,
                "phoneNumber" to phoneNumber,
                "imageUrl" to imageUri
            )
        } else {
            mapOf(
                "address" to address,
                "phoneNumber" to phoneNumber
            )
        }

        lifecycleScope.launch {
            viewModel.saveUser(
                firebaseAuth.currentUser?.uid!!,
                updatedFields,
                password,
                oldPassword,
                email
            )
            viewModel.userSaved.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Success -> {
                        Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT)
                            .show()
                        binding.saveLoadingGif.visibility = View.GONE
                        binding.buttonSaveProfile.visibility = View.GONE
                        binding.editTextPassword.visibility = View.GONE
                        binding.editTextOldPassword.visibility = View.GONE
                        binding.editTextAddress.visibility = View.GONE
                        binding.editTextPhoneNum.visibility = View.GONE
                        binding.buttonEditProfile.visibility = View.VISIBLE
                    }

                    is ScreenState.Error -> {
                        binding.buttonSaveProfile.visibility = View.VISIBLE
                        binding.saveLoadingGif.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        authManager.removeUser()
        authManager.removeRole()
        requireActivity().startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
    }


}