package edu.cit.auditor.paluto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.ActivityUserProfileBinding
import edu.cit.auditor.paluto.databinding.DialogEditProfileBinding
import edu.cit.auditor.paluto.dto.UpdateProfileRequest
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchUserProfile()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnEditProfile.setOnClickListener { showEditDialog() }
    }

    private fun fetchUserProfile() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getCurrentUser()
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data?.user
                    if (user != null) {
                        displayUserInfo(
                            user.firstname,
                            user.lastname,
                            user.email,
                            user.address ?: "",
                            user.role ?: ""
                        )
                    }
                } else {
                    val error = NetworkUtils.parseError(response)
                    Toast.makeText(this@UserProfileActivity, error, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserProfileActivity, "Error connecting to server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayUserInfo(fName: String, lName: String, email: String, address: String, role: String) {
        binding.apply {
            tvFullName.text = "$fName $lName"
            tvFirstName.text = fName
            tvLastName.text = lName
            tvEmail.text = "$email (cannot be changed)"
            tvAddress.text = if (address.isEmpty()) "Not set" else address
            tvRole.text = role.uppercase()

            val initials = "${fName.take(1)}${lName.take(1)}".uppercase()
            tvInitials.text = initials
        }
    }

    private fun showEditDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(LayoutInflater.from(this))

        // Pre-fill current data
        dialogBinding.etFirstName.setText(binding.tvFirstName.text)
        dialogBinding.etLastName.setText(binding.tvLastName.text)
        dialogBinding.etAddress.setText(if (binding.tvAddress.text == "Not set") "" else binding.tvAddress.text)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSave.setOnClickListener {
            val newFName = dialogBinding.etFirstName.text.toString().trim()
            val newLName = dialogBinding.etLastName.text.toString().trim()
            val newAddress = dialogBinding.etAddress.text.toString().trim()

            if (newFName.isEmpty() || newLName.isEmpty() || newAddress.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performUpdate(newFName, newLName, newAddress, dialog)
        }

        dialog.show()
    }

    private fun performUpdate(fName: String, lName: String, address: String, dialog: AlertDialog) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.updateProfile(
                    UpdateProfileRequest(fName, lName, address)
                )
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@UserProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    // Update SharedPreferences in case name changed
                    val sharedPref = getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putString("USER_NAME", fName)
                        apply()
                    }

                    // Refresh UI
                    fetchUserProfile()
                    dialog.dismiss()
                } else {
                    val error = NetworkUtils.parseError(response)
                    Toast.makeText(this@UserProfileActivity, error, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UserProfileActivity, "Error updating profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
