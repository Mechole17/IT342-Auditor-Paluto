package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.ActivityCustomerRegistrationPageBinding
import edu.cit.auditor.paluto.dto.CustomerRegistrationRequest
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerRegistrationPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerRegistrationPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            validateAndRegister()
        }

        binding.tvLoginRedirect.setOnClickListener {
            finish() // Return to Landing screen
        }
    }

    private fun validateAndRegister() {
        val fName = binding.etFirstName.text.toString().trim()
        val lName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        val address = binding.etAddress.text.toString().trim()

        if (fName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Email Format Validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus() // Focus back on the email field
            return
        }

        // 1. Basic length check
        if (password.length < 8) {
            Toast.makeText(this@CustomerRegistrationPageActivity, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            clearPasswordFields()
            return
        }


        // 2. Complexity check (Regex)
        // Matches: 1 Upper, 1 Lower, 1 Number, 1 Special Character
            val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$".toRegex()

            if (!passwordPattern.matches(password)) {
                Toast.makeText(this@CustomerRegistrationPageActivity,
                    "Password must have 1 Uppercase, 1 Lowercase, 1 Number, and 1 Special Character (@$!%*?&)",
                    Toast.LENGTH_LONG).show()
                clearPasswordFields()
                return
            }


        if (password != confirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            clearPasswordFields()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            setUIState(true)
            try {
                // Hardcoded "CUSTOMER" role for this specific activity
                val request = CustomerRegistrationRequest(fName, lName, email, password, address, "CUSTOMER","LOCAL")
                val response = RetrofitClient.instance.registerCustomer(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    // 1. Extract the data (The API sends the token here now)
                    val loginData = response.body()?.data

                    if (loginData != null) {
                        // 2. Save Session to SharedPreferences (Requirement 7.2)
                        val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("JWT_TOKEN", loginData.accessToken)
                            putString("USER_ROLE", "CUSTOMER") // We know they are a customer here
                            putString("USER_NAME", loginData.user.firstname)
                            apply()
                        }

                        // 3. Direct Redirect to Customer Dashboard
                        Toast.makeText(this@CustomerRegistrationPageActivity, "Welcome to Paluto, ${loginData.user.firstname}!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@CustomerRegistrationPageActivity, CustomerDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val error = NetworkUtils.parseError(response)
                    Toast.makeText(this@CustomerRegistrationPageActivity, error, Toast.LENGTH_SHORT).show()
                    setUIState(false)
                    clearPasswordFields()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CustomerRegistrationPageActivity, "Unable to connect to server.", Toast.LENGTH_SHORT).show()
                setUIState(false)
            }
        }
    }
    private fun clearPasswordFields() {
        binding.etPassword.text.clear()
        binding.etConfirmPassword.text.clear()
        binding.etPassword.requestFocus() // Put the cursor back on password for the user
    }

    private fun setUIState(isRegistering: Boolean) {
        binding.btnSignUp.apply {
            if (isRegistering) {
                isEnabled = false
                alpha = 0.5f // Greys out the button
                text = "signing up..."
                setTextColor(android.graphics.Color.BLACK)
            } else {
                isEnabled = true
                alpha = 1.0f // Returns to full color
                text = "Sign Up"
            }
        }
    }
}