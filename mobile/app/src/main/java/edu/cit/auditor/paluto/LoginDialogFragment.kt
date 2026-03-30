package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.DialogLoginBinding
import edu.cit.auditor.paluto.dto.LoginRequest
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginDialogFragment : DialogFragment() {

    private lateinit var binding: DialogLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogLoginBinding.inflate(inflater, container, false)

        binding.btnLoginSubmit.setOnClickListener {
            performLogin()
        }

        return binding.root
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Use Coroutines for API call
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    // Inside performLogin() after response.isSuccessful
                    val loginData = response.body()?.data
                    val role = loginData?.user?.role // This will be "CUSTOMER" or "COOK"

                    // 1. Unified Role Check
                    val intent = when (role) {
                        "COOK" -> { Toast.makeText(context, "Successful cook login", Toast.LENGTH_LONG).show()
                                    Intent(requireContext(), CookDashboardActivity::class.java)
                        }
                        "CUSTOMER" -> { Toast.makeText(context, "Successful customer login", Toast.LENGTH_LONG).show()
                                        Intent(requireContext(), CustomerDashboardActivity::class.java)
                        }
                        "ADMIN" -> {
                            Toast.makeText(context, "Admin access is restricted to the Web Portal.", Toast.LENGTH_LONG).show()
                            performLogout() // Clears any partial session
                            return@launch // Exits the coroutine
                        }
                        else -> {
                            Toast.makeText(context, "Account status: $role. Access pending.", Toast.LENGTH_SHORT).show()
                            null
                        }
                    }

                    // 2. If we have a valid mobile intent, save and go
                    if (loginData != null && intent != null) {
                        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("JWT_TOKEN", loginData.accessToken)
                            putString("USER_ROLE", role)
                            putString("USER_NAME", loginData.user.firstname)
                            apply()
                        }

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        dismiss()
                    }
                } else {
                    val err_msg = NetworkUtils.parseError(response)
                    Toast.makeText(context, err_msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to connect to server.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogout() {
        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply() // Clear JWT and Role
        dismiss()
    }

    // Makes the dialog background transparent so the CardView corners show
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}