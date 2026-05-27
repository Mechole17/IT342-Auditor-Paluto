package edu.cit.auditor.paluto.users.cook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import edu.cit.auditor.paluto.services.ManageServiceDialogFragment
import edu.cit.auditor.paluto.certificate.UploadCertificateDialogFragment
import edu.cit.auditor.paluto.databinding.FragmentCookPortfolioBinding
import edu.cit.auditor.paluto.certificate.CertificateResponse
import edu.cit.auditor.paluto.rating.RatingResponse
import edu.cit.auditor.paluto.services.ServiceResponse
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CookPortfolioFragment : Fragment() {

    private var _binding: FragmentCookPortfolioBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PortfolioAdapter

    private var services: List<ServiceResponse> = emptyList()
    private var certificates: List<CertificateResponse> = emptyList()
    private var reviews: List<RatingResponse> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCookPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabs()
        fetchData()

        binding.fabAdd.setOnClickListener {
            when (binding.tabLayout.selectedTabPosition) {
                0 -> showManageServiceDialog(null)
                1 -> showUploadCertDialog()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PortfolioAdapter(
            onEditService = { service -> showManageServiceDialog(service) },
            onDeleteCertificate = { cert -> confirmDeleteCertificate(cert) }
        )
        binding.rvPortfolio.layoutManager = LinearLayoutManager(context)
        binding.rvPortfolio.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateUIForTab(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchData() {
        val context = context ?: return
        if (!NetworkUtils.isNetworkAvailable(context)) {
            binding.tvEmpty.text = "No internet connection"
            binding.tvEmpty.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            return
        }

        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("USER_ID", -1)
        if (userId == -1L) return

        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val servicesDef = async { RetrofitClient.instance.getMyServices() }
                val certsDef = async { RetrofitClient.instance.getMyCertificates() }
                val reviewsDef = async { RetrofitClient.instance.getCookRatings(userId) }

                val sRes = servicesDef.await()
                val cRes = certsDef.await()
                val rRes = reviewsDef.await()

                if (_binding == null) return@launch

                services = if (sRes.isSuccessful) sRes.body()?.data ?: emptyList() else emptyList()
                certificates = if (cRes.isSuccessful) cRes.body()?.data ?: emptyList() else emptyList()
                reviews = if (rRes.isSuccessful) rRes.body()?.data ?: emptyList() else emptyList()

                binding.tabLayout.getTabAt(2)?.text = "Reviews (${reviews.size})"
                updateUIForTab(binding.tabLayout.selectedTabPosition)

            } catch (e: Exception) {
                if (_binding != null) {
                    if (e !is CancellationException) {
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }
            } finally {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUIForTab(position: Int) {
        binding.fabAdd.visibility = if (position == 2) View.GONE else View.VISIBLE
        val items = when (position) {
            0 -> services.map { PortfolioItem.Service(it) }
            1 -> certificates.map { PortfolioItem.Certificate(it) }
            else -> reviews.map { PortfolioItem.Review(it) }
        }
        adapter.setItems(items)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showManageServiceDialog(service: ServiceResponse?) {
        val dialog = ManageServiceDialogFragment.Companion.newInstance(service) {
            fetchData()
        }
        dialog.show(childFragmentManager, "manage_service")
    }

    private fun showUploadCertDialog() {
        val dialog = UploadCertificateDialogFragment.Companion.newInstance {
            fetchData()
        }
        dialog.show(childFragmentManager, "upload_certificate")
    }

    private fun confirmDeleteCertificate(cert: CertificateResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Certificate")
            .setMessage("Are you sure you want to remove this certificate?")
            .setPositiveButton("Remove") { _, _ -> deleteCertificate(cert.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCertificate(id: Long) {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitClient.instance.deleteCertificate(id)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Certificate removed", Toast.LENGTH_SHORT).show()
                    fetchData()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting certificate", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}