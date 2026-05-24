package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.auditor.paluto.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check for existing session
        val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null)
        val role = sharedPref.getString("USER_ROLE", null)

        if (token != null && role != null) {
            val intent = when (role) {
                "COOK" -> Intent(this, CookLandingActivity::class.java)
                "CUSTOMER" -> Intent(this, CustomerLandingActivity::class.java)
                else -> null
            }

            if (intent != null) {
                startActivity(intent)
                finish()
                return 
            } else {
                // Invalid role, clear session and proceed to show landing page
                sharedPref.edit().clear().apply()
            }
        }

        // Ensure ViewBinding is working
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 1. Open Registration as Cook
        binding.cookRegisterButton.setOnClickListener {
            val intent = Intent(this, CookRegistrationActivity::class.java)
            intent.putExtra("ROLE_TYPE", "COOK")
            startActivity(intent)
        }

        // 2. Open Registration as Customer
        binding.customerRegisterButton.setOnClickListener {
            val intent = Intent(this, CustomerRegistrationPageActivity::class.java)
            intent.putExtra("ROLE_TYPE", "CUSTOMER")
            startActivity(intent)
        }

        // 3. Open the Login Modal (DialogFragment)
        binding.signInButton.setOnClickListener {
            Toast.makeText(this, "Opening Login...", Toast.LENGTH_SHORT).show()
            val loginModal = LoginDialogFragment()
            loginModal.show(supportFragmentManager, "login_modal")
        }
    }
    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "COOK" -> Intent(this, CookLandingActivity::class.java)
            "CUSTOMER" -> Intent(this, CustomerLandingActivity::class.java)
            else -> null
        }

        if (intent != null) {
            startActivity(intent)
            finish() // Crucial: Remove LandingActivity from the backstack
        }
    }
}