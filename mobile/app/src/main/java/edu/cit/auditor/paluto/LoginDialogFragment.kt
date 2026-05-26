package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.DialogLoginBinding
import edu.cit.auditor.paluto.dto.GoogleLoginRequest
import edu.cit.auditor.paluto.dto.LoginRequest
import edu.cit.auditor.paluto.dto.LoginResponse
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

        binding.btnGoogleLogin.setOnClickListener {
            performGoogleLogin()
        }

        return binding.root
    }

    private fun performGoogleLogin() {
        val credentialManager = CredentialManager.create(requireContext())

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.google_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                
                val credential = result.credential
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    sendGoogleTokenToServer(idToken)
                } else {
                    Log.e("GoogleLogin", "Unexpected credential type: ${credential.type}")
                }
            } catch (e: GetCredentialException) {
                Log.e("GoogleLogin", "Error getting credential", e)
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendGoogleTokenToServer(idToken: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            setUIState(true)
            try {
                val response = RetrofitClient.instance.googleLogin(GoogleLoginRequest(idToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginData = response.body()?.data
                    if (loginData?.accessToken != null) {
                        // User exists and is logged in
                        handleSuccessfulLogin(loginData)
                    } else if (loginData?.user != null) {
                        // New user, need to register
                        Toast.makeText(requireContext(), "Welcome ${loginData.user.firstname}! Please complete your registration.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("GoogleLogin", "Server error", e)
                Toast.makeText(requireContext(), "Unable to connect to server.", Toast.LENGTH_SHORT).show()
            } finally {
                setUIState(false)
            }
        }
    }

    private fun handleSuccessfulLogin(loginData: LoginResponse) {
        val user = loginData.user ?: return
        val role = user.role
        val intent = when (role) {
            "COOK" -> {
                Toast.makeText(requireContext(), "Successful cook login", Toast.LENGTH_LONG).show()
                Intent(requireContext(), CookLandingActivity::class.java)
            }
            "CUSTOMER" -> {
                Toast.makeText(requireContext(), "Successful customer login", Toast.LENGTH_LONG).show()
                Intent(requireContext(), CustomerLandingActivity::class.java)
            }
            "ADMIN" -> {
                Toast.makeText(requireContext(), "Admin access is restricted to the Web Portal.", Toast.LENGTH_LONG).show()
                performLogout()
                null
            }
            else -> {
                Toast.makeText(requireContext(), "Account status: $role. Access pending.", Toast.LENGTH_SHORT).show()
                null
            }
        }

        if (intent != null) {
            val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().apply {
                putString("JWT_TOKEN", loginData.accessToken)
                putString("USER_ROLE", role)
                putString("USER_NAME", user.firstname)
                putLong("USER_ID", user.id)
                apply()
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dismiss()
        } else {
            setUIState(false)
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Use lifecycleScope for API call
        viewLifecycleOwner.lifecycleScope.launch {
            setUIState(true)
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginData = response.body()?.data
                    if (loginData != null) {
                        handleSuccessfulLogin(loginData)
                    } else {
                        setUIState(false)
                    }
                } else {
                    val errMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
                    clearPass()
                    setUIState(false)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to connect to server.", Toast.LENGTH_SHORT).show()
                setUIState(false)
            }
        }
    }
    private fun clearPass(){
        binding.etPassword.setText("")
        binding.etPassword.requestFocus()
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
    private fun setUIState(isRegistering: Boolean) {
        binding.btnLoginSubmit.apply {
            if (isRegistering) {
                isEnabled = false
                alpha = 0.9f // Greys out the button
                text = "signing in..."

            } else {
                isEnabled = true
                alpha = 1.0f // Returns to full color
                text = "sign in"
            }
        }
    }
}