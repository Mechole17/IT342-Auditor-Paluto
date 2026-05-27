package edu.cit.auditor.paluto.booking

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
import edu.cit.auditor.paluto.databinding.FragmentCookBookingsBinding
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CookBookingsFragment : Fragment() {

    private var _binding: FragmentCookBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingAdapter: BookingAdapter
    private var allBookings: List<BookingResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCookBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()
        fetchBookings()
    }

    override fun onResume() {
        super.onResume()
        fetchBookings()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            emptyList(),
            userRole = "COOK",
            onDetailsClick = { booking -> showBookingDetails(booking) },
            onCancelClick = {},
            onRateClick = {},
            onAcceptClick = { booking -> handleStatusUpdate(booking, "ACCEPTED", "ACCEPT") },
            onRejectClick = { booking ->
                handleStatusUpdate(
                    booking,
                    "REJECTED_REFUNDED",
                    "REJECT"
                )
            },
            onCompleteClick = { booking -> handleStatusUpdate(booking, "COMPLETED", "COMPLETE") }
        )
        binding.rvBookings.apply {
            adapter = bookingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterBookings(tab?.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchBookings() {
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
                val response = RetrofitClient.instance.getCookBookings(userId)

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    allBookings = response.body()?.data ?: emptyList()
                    filterBookings(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.text.toString())
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    if (e !is CancellationException) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun filterBookings(tabName: String) {
        val filtered = allBookings.filter { b ->
            val status = b.status.uppercase()
            when (tabName) {
                "Pending" -> status == "PAID_PENDING"
                "Active" -> status == "ACCEPTED"
                "History" -> listOf("COMPLETED", "REJECTED_REFUNDED", "CANCELLED_REFUNDED").contains(status)
                else -> false
            }
        }
        bookingAdapter.updateBookings(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun handleStatusUpdate(booking: BookingResponse, newStatus: String, action: String) {
        val message = when (action) {
            "ACCEPT" -> "Are you sure you want to accept this booking? You are committing to this schedule."
            "REJECT" -> "Are you sure you want to reject this booking? The customer will be refunded."
            "COMPLETE" -> "Are you sure you want to mark this booking as completed?"
            else -> "Are you sure?"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Action")
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                performStatusUpdate(booking.id, newStatus, action)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performStatusUpdate(id: Long, newStatus: String, action: String) {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.updateBookingStatus(id, newStatus, action)

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "Booking updated successfully", Toast.LENGTH_SHORT).show()
                    fetchBookings()
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showBookingDetails(booking: BookingResponse) {
        val dialog = BookingDetailsDialogFragment.Companion.newInstance(booking.id)
        dialog.show(childFragmentManager, "booking_details")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}