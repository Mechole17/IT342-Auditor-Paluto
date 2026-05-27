package edu.cit.auditor.paluto.users.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import edu.cit.auditor.paluto.services.MealDetailsActivity
import edu.cit.auditor.paluto.users.customer.HomeMealAdapter
import edu.cit.auditor.paluto.databinding.FragmentCustomerHomeBinding
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CustomerHomeFragment : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeMealAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchMeals()
    }

    private fun setupRecyclerView() {
        adapter = HomeMealAdapter(emptyList()) { meal ->
            val intent = Intent(requireContext(), MealDetailsActivity::class.java)
            intent.putExtra("SERVICE_ID", meal.id)
            startActivity(intent)
        }
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@CustomerHomeFragment.adapter
        }
    }

    private fun fetchMeals() {
        val context = context ?: return
        if (!NetworkUtils.isNetworkAvailable(context)) {
            binding.tvEmpty.text = "No internet connection"
            binding.tvEmpty.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllServices()

                // Safety check: Is the fragment still attached and visible?
                if (_binding == null) return@launch

                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    val meals = response.body()?.data ?: emptyList()
                    adapter.updateMeals(meals)
                    binding.tvEmpty.visibility = if (meals.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    if (e !is CancellationException) {
                        Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}