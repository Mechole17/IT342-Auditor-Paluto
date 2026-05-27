package edu.cit.auditor.paluto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.auditor.paluto.R
import edu.cit.auditor.paluto.databinding.ItemBookingBinding
import edu.cit.auditor.paluto.dto.BookingResponse

class BookingAdapter(
    private var bookings: List<BookingResponse>,
    private val userRole: String,
    private val isDashboard: Boolean = false,
    private val onDetailsClick: (BookingResponse) -> Unit,
    private val onCancelClick: (BookingResponse) -> Unit,
    private val onRateClick: (BookingResponse) -> Unit,
    private val onAcceptClick: (BookingResponse) -> Unit = {},
    private val onRejectClick: (BookingResponse) -> Unit = {},
    private val onCompleteClick: (BookingResponse) -> Unit = {}
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    private var ratedMap: Map<Long, Boolean> = emptyMap()

    fun updateRatedMap(newRatedMap: Map<Long, Boolean>) {
        ratedMap = newRatedMap
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.binding.apply {
            tvServiceTitle.text = booking.serviceTitle
            
            // Handle name based on role
            if (userRole == "COOK") {
                tvCookName.text = booking.customerName ?: "Customer"
            } else {
                tvCookName.text = booking.cookName ?: "Cook"
            }

            tvQuantity.text = "x${booking.quantity}"
            tvStatus.text = booking.status.replace("_", " ").lowercase()
            tvDate.text = booking.scheduledDate
            tvTime.text = booking.scheduledTime

            if (!booking.serviceImage.isNullOrEmpty()) {
                ivServiceImage.load(booking.serviceImage) {
                    crossfade(true)
                    placeholder(R.drawable.ramen)
                    error(R.drawable.ramen)
                }
            } else {
                ivServiceImage.setImageResource(R.drawable.ramen)
            }

            // Status color
            val statusColor = when {
                booking.status.contains("PENDING") -> 0xFFEC9812.toInt()
                booking.status.contains("COMPLETED") -> 0xFF28A745.toInt()
                booking.status.contains("REJECTED") || booking.status.contains("CANCELLED") -> 0xFFD10B04.toInt()
                else -> 0xFF28A745.toInt()
            }
            tvStatus.setTextColor(statusColor)

            // Button Visibility
            btnCancel.visibility = if (userRole == "CUSTOMER" && booking.status.uppercase() == "PAID_PENDING") View.VISIBLE else View.GONE
            
            val isCompleted = booking.status.uppercase() == "COMPLETED"
            val hasBeenRated = ratedMap[booking.id] ?: false
            
            btnRate.visibility = if (userRole == "CUSTOMER" && isCompleted) View.VISIBLE else View.GONE
            btnRate.isEnabled = !hasBeenRated
            btnRate.text = if (hasBeenRated) "Rated" else "Rate"
            btnRate.backgroundTintList = android.content.res.ColorStateList.valueOf(if (hasBeenRated) 0xFFCCCCCC.toInt() else 0xFFF5A623.toInt())

            // Cook Actions
            val isPaidPending = booking.status.uppercase() == "PAID_PENDING"
            val isAccepted = booking.status.uppercase() == "ACCEPTED"
            
            btnAccept.visibility = if (userRole == "COOK" && isPaidPending && !isDashboard) View.VISIBLE else View.GONE
            btnReject.visibility = if (userRole == "COOK" && isPaidPending && !isDashboard) View.VISIBLE else View.GONE
            
            val scheduleMet = isScheduleMet(booking.scheduledDate, booking.scheduledTime)
            btnComplete.visibility = if (userRole == "COOK" && isAccepted && !isDashboard) View.VISIBLE else View.GONE
            btnComplete.isEnabled = scheduleMet
            btnComplete.alpha = if (scheduleMet) 1.0f else 0.5f
            btnComplete.text = if (scheduleMet) "Mark as Completed" else "Locked (Time not met)"

            btnDetails.setOnClickListener { onDetailsClick(booking) }
            btnCancel.setOnClickListener { onCancelClick(booking) }
            btnRate.setOnClickListener { onRateClick(booking) }
            btnAccept.setOnClickListener { onAcceptClick(booking) }
            btnReject.setOnClickListener { onRejectClick(booking) }
            btnComplete.setOnClickListener { onCompleteClick(booking) }
        }
    }

    override fun getItemCount() = bookings.size

    private fun isScheduleMet(dateStr: String?, timeStr: String?): Boolean {
        if (dateStr == null || timeStr == null) return false
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)
            val schedule = sdf.parse("$dateStr $timeStr")
            val now = java.util.Date()
            schedule != null && now.after(schedule)
        } catch (e: Exception) {
            false
        }
    }

    fun updateBookings(newBookings: List<BookingResponse>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
