package edu.cit.auditor.paluto.users.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.auditor.paluto.databinding.FragmentCustomerCooksBinding
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import edu.cit.auditor.paluto.users.cook.CookAdapter
import kotlinx.coroutines.launch

class CustomerCooksFragment : Fragment() {

    private var _binding: FragmentCustomerCooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var cookAdapter: CookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerCooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchCooks()
    }

    private fun setupRecyclerView() {
        cookAdapter = CookAdapter(emptyList())
        binding.rvCooks.apply {
            adapter = cookAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun fetchCooks() {
        val context = context ?: return
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllCooks()

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    val cooks = response.body()?.data ?: emptyList()
                    cookAdapter.updateCooks(cooks)
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}