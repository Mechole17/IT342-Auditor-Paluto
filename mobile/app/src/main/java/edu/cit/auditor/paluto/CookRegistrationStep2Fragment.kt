package edu.cit.auditor.paluto

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.FragmentCookRegStep2Binding
import edu.cit.auditor.paluto.dto.CookRegistrationRequest
import edu.cit.auditor.paluto.model.CookRegistrationViewModel
import kotlinx.coroutines.launch

class CookRegistrationStep2Fragment : Fragment() {

    private lateinit var binding: FragmentCookRegStep2Binding
    private val viewModel: CookRegistrationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCookRegStep2Binding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            viewModel.bio = binding.etBio.text.toString()
            viewModel.hourlyRate = binding.etRate.text.toString().toDoubleOrNull() ?: 0.0

            (activity as? CookRegistrationActivity)?.moveToPreviousStep()
        }

        binding.btnFinish.setOnClickListener {
            val rateText = binding.etRate.text.toString()
            val expText = binding.etExperience.text.toString()
            val bio = binding.etBio.text.toString().trim()

            // 1. Number Constraints
            val rate = rateText.toDoubleOrNull() ?: 0.0
            val exp = expText.toIntOrNull() ?: 0

            if (rate < 100.0) {
                Toast.makeText(requireContext(), "Minimum rate is 100 PHP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (exp < 1) {
                Toast.makeText(requireContext(), "Minimum 1 year experience", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. PERFORM FINAL REGISTRATION
            performFinalRegistration(rate, exp, bio)
        }

        return binding.root
    }
    private fun performFinalRegistration(rate: Double, exp: Int, bio: String) {
        // 1. Show Loading (Optional but recommended)
        binding.btnFinish.isEnabled = false
        binding.btnFinish.alpha = 0.5f
        lifecycleScope.launch {
            try {
                // COMBINE DATA: Identity (ViewModel) + Professional (Local)
                val request = CookRegistrationRequest(
                    firstname = viewModel.firstName,
                    lastname = viewModel.lastName,
                    email = viewModel.email,
                    password = viewModel.password,
                    address = viewModel.address,
                    bio = bio,
                    hourly_rate = rate,
                    years_xp = exp,
                    role = "COOK",
                    auth_provider = "LOCAL"
                )

                val response = RetrofitClient.instance.registerCook(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginData = response.body()?.data
                    if (loginData != null) {
                        // 2. Save Session to SharedPreferences
                        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", android.content.Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("JWT_TOKEN", loginData.accessToken)
                            putString("USER_ROLE", "COOK")
                            putString("USER_NAME", loginData.user.firstname)
                            apply()
                        }

                        Toast.makeText(requireContext(), "Welcome Chef ${loginData.user.firstname}!", Toast.LENGTH_LONG).show()

                        // 3. Redirect to Cook Dashboard
                        val intent = Intent(requireContext(), CookDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } else {
                    // 1. Use the global helper to get the error message
                    val errorMsg = edu.cit.auditor.paluto.utils.NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()

                    binding.btnFinish.isEnabled = true
                    binding.btnFinish.alpha = 1.0f

                    // 2. If it's an email/account conflict, clear data and go back to Step 1
                    if (errorMsg.contains("email", ignoreCase = true) || errorMsg.contains("exists", ignoreCase = true)) {

                        // Clear the password in the ViewModel (the signal for Step 1)
                        viewModel.password = ""

                        // Slide back to Step 1 so they can fix the email
                        (activity as? CookRegistrationActivity)?.moveToPreviousStep()
                    } else {
                        // For other errors, just re-enable the current button
                        binding.btnFinish.isEnabled = true
                        binding.btnFinish.alpha = 1.0f
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                binding.btnFinish.isEnabled = true
                binding.btnFinish.alpha = 1.0f
            }
        }
    }
}