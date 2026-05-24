package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.cit.auditor.paluto.databinding.ActivityCustomerLandingBinding

class CustomerLandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle possible deep link
        handleIntent(intent)

        // 1. Session & Role Verification
        val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "")

        if (role != "CUSTOMER") {
            redirectToLanding()
            return
        }

        // 2. Set default fragment or handle redirect
        if (savedInstanceState == null) {
            val targetTab = intent.getStringExtra("SELECT_TAB")
            if (targetTab == "BOOKINGS") {
                binding.bottomNavigation.selectedItemId = R.id.nav_bookings
                replaceFragment(CustomerBookingsFragment())
            } else {
                replaceFragment(CustomerHomeFragment())
            }
        }

        setupBottomNavigation()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val targetTab = intent.getStringExtra("SELECT_TAB")
        val data = intent.data

        if (targetTab == "BOOKINGS" || data?.scheme == "paluto") {
            binding.bottomNavigation.selectedItemId = R.id.nav_bookings
            replaceFragment(CustomerBookingsFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(CustomerHomeFragment())
                    true
                }
                R.id.nav_cooks -> {
                    replaceFragment(CustomerCooksFragment())
                    true
                }
                R.id.nav_bookings -> {
                    replaceFragment(CustomerBookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(CustomerProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun redirectToLanding() {
        val intent = Intent(this, LandingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}