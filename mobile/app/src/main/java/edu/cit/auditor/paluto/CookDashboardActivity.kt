package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cit.auditor.paluto.databinding.ActivityCookDashboardBinding
import edu.cit.auditor.paluto.databinding.ActivityCustomerDashboardBinding

class CookDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCookDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCookDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Session & Role Verification
        val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "")

        if (role != "COOK") {
            // Security: Redirect unauthorized roles back to Landing
            redirectToLanding()
            return
        }

        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply() // Clear JWT and Role
        redirectToLanding()
    }

    private fun redirectToLanding() {
        val intent = Intent(this, LandingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}