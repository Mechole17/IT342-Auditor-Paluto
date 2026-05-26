package edu.cit.auditor.paluto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.cit.auditor.paluto.databinding.FragmentCookProfileBinding

class CookProfileFragment : Fragment() {

    private var _binding: FragmentCookProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCookProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "Cook")
        binding.tvUserName.text = userName

        binding.btnViewProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UserProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val logoutModal = LogoutDialogFragment()
            logoutModal.show(parentFragmentManager, "logout_confirmation")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}