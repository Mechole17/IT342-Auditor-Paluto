package edu.cit.auditor.paluto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cit.auditor.paluto.adapter.CookRegPagerAdapter
import edu.cit.auditor.paluto.databinding.ActivityCookRegistrationBinding

class CookRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCookRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCookRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = CookRegPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Disable manual swiping for a controlled wizard flow
        binding.viewPager.isUserInputEnabled = false
    }

    // Helper functions for Fragments to call
    fun moveToNextStep() {
        binding.viewPager.currentItem = 1
    }

    fun moveToPreviousStep() {
        binding.viewPager.currentItem = 0
    }
}