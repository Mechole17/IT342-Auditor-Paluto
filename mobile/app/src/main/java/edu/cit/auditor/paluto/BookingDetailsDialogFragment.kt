package edu.cit.auditor.paluto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cit.auditor.paluto.adapter.TimelineAdapter
import edu.cit.auditor.paluto.adapter.TimelineStep
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.DialogBookingDetailsBinding
import edu.cit.auditor.paluto.dto.BookingResponse
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailsDialogFragment : DialogFragment() {

    private var _binding: DialogBookingDetailsBinding? = null
    private val binding get() = _binding!!
    private var bookingId: Long = -1

    companion object {
        fun newInstance(bookingId: Long): BookingDetailsDialogFragment {
            val args = Bundle().apply { putLong("booking_id", bookingId) }
            return BookingDetailsDialogFragment().apply { arguments = args }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingId = arguments?.getLong("booking_id") ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnClose.setOnClickListener { dismiss() }
        fetchBookingDetails()
    }

    private fun fetchBookingDetails() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getBookingById(bookingId)
                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { displayDetails(it) }
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to load details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayDetails(booking: BookingResponse) {
        val sharedPref = requireActivity().getSharedPreferences("PalutoPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "")
        
        binding.apply {
            tvServiceTitle.text = "${booking.serviceTitle} (x${booking.quantity})"
            tvCounterpartyLabel.text = if (role == "COOK") "CUSTOMER" else "COOK"
            tvCounterpartyName.text = if (role == "COOK") booking.customerName else booking.cookName
            tvSchedule.text = "${booking.scheduledDate} at ${booking.scheduledTime}"
            tvAddress.text = booking.serviceAddress ?: "No address provided"
            tvAmount.text = "₱${String.format("%,.2f", booking.totalAmount ?: 0.0)}"
            
            setupTimeline(booking)
        }
    }

    private fun setupTimeline(booking: BookingResponse) {
        val steps = mutableListOf<TimelineStep>()
        val status = booking.status.uppercase()

        // 1. Payment Confirmed (Always first)
        steps.add(TimelineStep("Payment Confirmed", formatDate(booking.createdAt) ?: "Booking is secured.", true))

        if (status == "REJECTED" || status == "REJECTED_REFUNDED" || status == "CANCELLED" || status == "CANCELLED_REFUNDED") {
            val label = if (status.contains("REJECTED")) "Booking Rejected" else "Booking Cancelled"
            val date = if (status.contains("REJECTED")) booking.rejectedAt else booking.cancelledAt
            val desc = if (status.contains("REJECTED")) "Service declined by cook." else "Booking cancelled by user."
            
            steps.add(TimelineStep(label, formatDate(date) ?: desc, true, isError = true))
            steps.add(TimelineStep("Refund Processed", "Payment returned to customer.", true, isError = true, isLast = true))
        } else {
            // 2. Booking Accepted
            val isAccepted = status == "ACCEPTED" || status == "COMPLETED"
            steps.add(TimelineStep("Booking Accepted", formatDate(booking.acceptedAt) ?: "Awaiting confirmation...", isAccepted))
            
            // 3. Service Completed
            val isCompleted = status == "COMPLETED"
            steps.add(TimelineStep("Service Completed", formatDate(booking.completedAt) ?: "Ready for service date.", isCompleted, isLast = true))
        }

        binding.rvTimeline.apply {
            adapter = TimelineAdapter(steps)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun formatDate(dateStr: String?): String? {
        if (dateStr == null) return null
        return try {
            // Adjust input format based on backend response
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val outputFormat = SimpleDateFormat("MMM d, h:mm a", Locale.US)
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
