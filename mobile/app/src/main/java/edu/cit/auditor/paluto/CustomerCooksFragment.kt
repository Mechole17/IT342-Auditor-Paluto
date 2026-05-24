package edu.cit.auditor.paluto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.auditor.paluto.adapter.CookAdapter
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.FragmentCustomerCooksBinding
import edu.cit.auditor.paluto.utils.NetworkUtils
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
        binding.progressBar.visibility = View.VISIBLE
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllCooks()
                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val cooks = response.body()?.data ?: emptyList()
                    cookAdapter.updateCooks(cooks)
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
