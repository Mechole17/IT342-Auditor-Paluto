package edu.cit.auditor.paluto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import edu.cit.auditor.paluto.databinding.FragmentCookRegStep1Binding
import edu.cit.auditor.paluto.model.CookRegistrationViewModel

class CookRegistrationStep1Fragment : Fragment() {

    private lateinit var binding: FragmentCookRegStep1Binding
    // This connects this Fragment to the shared data box
    private val viewModel: CookRegistrationViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()

        // If Step 2 cleared the password in the ViewModel, reset the UI fields
        if (viewModel.password.isEmpty()) {
            binding.etPassword.text?.clear()
            binding.etConfirmPassword.text?.clear()

            // Focus on the email field so the user knows what to change
            binding.etEmail.requestFocus()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCookRegStep1Binding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val fName = binding.etFirstName.text.toString().trim()
            val lName = binding.etLastName.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()

            // 1. Validation
            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pass.isEmpty() || address.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(requireContext(), "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Check Password Length
            if (pass.length < 8) {
                Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                clearPasswordFields() // Call helper to clear UI
                return@setOnClickListener
            }

            // Check your Backend logic: 8 chars, 1 special, 1 upper, 1 lower
            val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()
            if (!passwordPattern.matches(pass)) {
                Toast.makeText(requireContext(), "Password must have 1 Uppercase, 1 Lowercase, 1 Number, and 1 Special Character (@\$!%*?&)", Toast.LENGTH_SHORT).show()
                clearPasswordFields()
                return@setOnClickListener
            }

            if (pass != confirm) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                clearPasswordFields()
                return@setOnClickListener
            }

            // 2. SAVE TO VIEWMODEL
            viewModel.firstName = binding.etFirstName.text.toString().trim()
            viewModel.lastName = binding.etLastName.text.toString().trim()
            viewModel.address = binding.etAddress.text.toString().trim()
            viewModel.email = email
            viewModel.password = pass

            // 3. Move to next page
            (activity as? CookRegistrationActivity)?.moveToNextStep()
        }

        binding.btnCancel.setOnClickListener {
            activity?.finish()
        }

        return binding.root
    }
    private fun clearPasswordFields() {
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
        binding.etPassword.requestFocus() // Put the cursor back where they start typing
    }
}