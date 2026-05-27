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
import edu.cit.auditor.paluto.rating.RateCookDialogFragment
import edu.cit.auditor.paluto.databinding.FragmentCustomerBookingsBinding
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CustomerBookingsFragment : Fragment() {

    private var _binding: FragmentCustomerBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingAdapter: BookingAdapter
    private var allBookings: List<BookingResponse> = emptyList()
    private val ratedMap = mutableMapOf<Long, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBookingsBinding.inflate(inflater, container, false)
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
            userRole = "CUSTOMER",
            onDetailsClick = { booking -> showBookingDetails(booking) },
            onCancelClick = { booking -> handleCancelBooking(booking) },
            onRateClick = { booking -> showRatingDialog(booking) }
        )
        binding.rvBookings.apply {
            adapter = bookingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleCancelBooking(booking: BookingResponse) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking? You will be refunded.")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                performCancel(booking.id)
            }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    private fun performCancel(bookingId: Long) {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.cancelBooking(bookingId)

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show()
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

    private fun showRatingDialog(booking: BookingResponse) {
        val dialog = RateCookDialogFragment.Companion.newInstance(booking.id, booking.cookName) {
            fetchBookings() // Refresh the whole list after rating
        }
        dialog.show(childFragmentManager, "rate_cook")
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
                val response = RetrofitClient.instance.getCustomerBookings(userId)

                if (_binding == null) return@launch
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    allBookings = response.body()?.data ?: emptyList()

                    // Check ratings for completed bookings
                    allBookings.filter { it.status.uppercase() == "COMPLETED" }.forEach {
                        checkIfRated(it.id)
                    }

                    filterBookings(binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.text.toString())
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    if (e !is CancellationException && e.message?.contains("cancelled", true) != true) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkIfRated(bookingId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.checkIfRated(bookingId)

                if (_binding == null) return@launch

                if (response.isSuccessful && response.body()?.success == true) {
                    ratedMap[bookingId] = response.body()?.data ?: false
                    bookingAdapter.updateRatedMap(ratedMap)
                }
            } catch (e: Exception) {
                // Ignore silent errors for check
            }
        }
    }

    private fun filterBookings(tabName: String) {
        val filtered = allBookings.filter { b ->
            val status = b.status.lowercase()
            when (tabName) {
                "Active" -> status == "paid_pending" || status == "accepted"
                "Completed" -> status == "completed"
                "Rejected" -> status == "rejected_refunded"
                "Cancelled" -> status == "cancelled_refunded"
                else -> false
            }
        }
        bookingAdapter.updateBookings(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
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