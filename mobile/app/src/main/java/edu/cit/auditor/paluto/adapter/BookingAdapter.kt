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
    private val onDetailsClick: (BookingResponse) -> Unit,
    private val onCancelClick: (BookingResponse) -> Unit,
    private val onRateClick: (BookingResponse) -> Unit
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
            tvCookName.text = booking.cookName
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
            btnCancel.visibility = if (booking.status.uppercase() == "PAID_PENDING") View.VISIBLE else View.GONE
            
            val isCompleted = booking.status.uppercase() == "COMPLETED"
            val hasBeenRated = ratedMap[booking.id] ?: false
            
            btnRate.visibility = if (isCompleted) View.VISIBLE else View.GONE
            btnRate.isEnabled = !hasBeenRated
            btnRate.text = if (hasBeenRated) "Rated" else "Rate"
            btnRate.backgroundTintList = android.content.res.ColorStateList.valueOf(if (hasBeenRated) 0xFFCCCCCC.toInt() else 0xFFF5A623.toInt())

            btnDetails.setOnClickListener { onDetailsClick(booking) }
            btnCancel.setOnClickListener { onCancelClick(booking) }
            btnRate.setOnClickListener { onRateClick(booking) }
        }
    }

    override fun getItemCount() = bookings.size

    fun updateBookings(newBookings: List<BookingResponse>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
