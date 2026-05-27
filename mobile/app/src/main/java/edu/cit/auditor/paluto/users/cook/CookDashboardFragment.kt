package edu.cit.auditor.paluto.users.cook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.auditor.paluto.booking.BookingAdapter
import edu.cit.auditor.paluto.booking.BookingDetailsDialogFragment
import edu.cit.auditor.paluto.booking.BookingResponse
import edu.cit.auditor.paluto.databinding.FragmentCookDashboardBinding
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CookDashboardFragment : Fragment() {

    private var _binding: FragmentCookDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCookDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchDashboardData()
    }

    override fun onResume() {
        super.onResume()
        fetchDashboardData()
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            emptyList(),
            userRole = "COOK",
            isDashboard = true,
            onDetailsClick = { booking -> showBookingDetails(booking) },
            onCancelClick = {}, // Cooks manage via Bookings tab
            onRateClick = {}
        )
        binding.rvActiveBookings.apply {
            adapter = bookingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun fetchDashboardData() {
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
                val statsDef = async { RetrofitClient.instance.getCookStats(userId) }
                val bookingsDef = async { RetrofitClient.instance.getCookBookings(userId) }

                val statsRes = statsDef.await()
                val bookingsRes = bookingsDef.await()

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (statsRes.isSuccessful && statsRes.body()?.success == true) {
                    val stats = statsRes.body()?.data
                    displayStats(stats)
                }

                if (bookingsRes.isSuccessful && bookingsRes.body()?.success == true) {
                    val allBookings = bookingsRes.body()?.data ?: emptyList()
                    val activeBookings = allBookings.filter { it.status.uppercase() == "ACCEPTED" }
                    bookingAdapter.updateBookings(activeBookings)
                    binding.tvEmpty.visibility = if (activeBookings.isEmpty()) View.VISIBLE else View.GONE
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

    private fun displayStats(stats: Map<String, Any>?) {
        if (stats == null) return
        binding.apply {
            // Map keys from backend to correct KPI cards
            tvAvgRating.text = String.format("%.1f", (stats["avgRating"] as? Number)?.toDouble() ?: 0.0)

            // Cast to Number then Int to remove trailing .0 from GSON parsing
            val completed = (stats["completedBookings"] as? Number)?.toInt() ?: 0
            tvCompletedCount.text = completed.toString()

            val earnings = (stats["totalEarnings"] as? Number)?.toDouble() ?: 0.0
            tvTotalEarnings.text = "Php ${String.format("%,.0f", earnings)}"
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