package edu.cit.auditor.paluto

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.ActivityBookingPaymentBinding
import edu.cit.auditor.paluto.dto.CheckoutRequest
import edu.cit.auditor.paluto.dto.ServiceResponse
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookingPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingPaymentBinding
    private var service: ServiceResponse? = null
    private var quantity: Int = 1
    
    private var selectedCalendar = Calendar.getInstance()
    private var bookedDates: List<String> = emptyList()
    
    private var totalAmount: Double = 0.0
    private var isPaymentStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isPaymentStarted = savedInstanceState?.getBoolean("isPaymentStarted", false) ?: false

        service = intent.getSerializableExtra("SERVICE_DATA") as? ServiceResponse
        quantity = intent.getIntExtra("QUANTITY", 1)

        if (service == null) {
            Toast.makeText(this, "Service data missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        calculateTotal()
        fetchBookedDates()

        binding.btnBack.setOnClickListener { finish() }
        binding.etDate.setOnClickListener { showMaterialDatePicker() }
        binding.etTime.setOnClickListener { showTimePicker() }
        
        binding.btnPayNow.setOnClickListener {
            handleCheckout()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPaymentStarted) {
            // When user returns from Custom Tab (closes it), go to Bookings
            val intent = Intent(this, CustomerLandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("SELECT_TAB", "BOOKINGS")
            startActivity(intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPaymentStarted", isPaymentStarted)
    }

    private fun setupUI() {
        service?.let { s ->
            binding.apply {
                tvMealTitle.text = s.title
                tvQuantity.text = "x$quantity"
                tvIngCost.text = "Php ${String.format("%,.0f", s.ingredientsCost * quantity)}"
                tvCookRate.text = "Php ${String.format("%,.0f", s.cookHourlyRate)}"
                
                if (!s.imageUrl.isNullOrEmpty()) {
                    ivMealImage.load(s.imageUrl) {
                        placeholder(R.drawable.ramen)
                        error(R.drawable.ramen)
                    }
                }
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("GCash", "Card"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPayment.adapter = adapter
    }

    private fun calculateTotal() {
        service?.let { s ->
            val ingredientCost = s.ingredientsCost * quantity
            
            val basePrepTime = s.estPrepTime.toDouble()
            val totalPrepTimeMinutes = if (quantity > 1) {
                basePrepTime + (basePrepTime * 0.20 * (quantity - 1))
            } else {
                basePrepTime
            }

            val laborCost = (totalPrepTimeMinutes / 60.0) * s.cookHourlyRate
            totalAmount = ingredientCost + laborCost

            binding.tvTotal.text = "Php ${String.format("%,.2f", totalAmount)}"
            
            val hours = totalPrepTimeMinutes.toInt() / 60
            val mins = totalPrepTimeMinutes.toInt() % 60
            binding.tvPrepTime.text = if (hours > 0) "$hours hr $mins mins" else "$mins mins"
        }
    }

    private fun fetchBookedDates() {
        service?.let { s ->
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.getBookedDates(s.cookId)
                    if (response.isSuccessful) {
                        bookedDates = response.body()?.data ?: emptyList()
                    }
                } catch (e: Exception) {
                    // Fail silently for dates
                }
            }
        }
    }

    private fun showMaterialDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(object : CalendarConstraints.DateValidator {
                override fun isValid(date: Long): Boolean {
                    // 1. Only allow future dates
                    if (!DateValidatorPointForward.from(System.currentTimeMillis()).isValid(date)) return false
                    
                    // 2. Disable already booked dates
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val dateStr = sdf.format(Date(date))
                    return !bookedDates.contains(dateStr)
                }
                override fun describeContents(): Int = 0
                override fun writeToParcel(dest: android.os.Parcel, flags: Int) {}
            })

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            selectedCalendar.time = date
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            binding.etDate.setText(sdf.format(date))
        }
        picker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun showTimePicker() {
        TimePickerDialog(this, { _, hour, minute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
            selectedCalendar.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("hh:mm a", Locale.US)
            binding.etTime.setText(sdf.format(selectedCalendar.time))
        }, 12, 0, false).show()
    }

    private fun handleCheckout() {
        val date = binding.etDate.text.toString()
        val time = binding.etTime.text.toString()
        val address = binding.etAddress.text.toString()

        if (date.isEmpty() || time.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Final safety check against booked dates
        if (bookedDates.contains(date)) {
            Toast.makeText(this, "This date was recently booked. Please select another.", Toast.LENGTH_LONG).show()
            binding.etDate.setText("")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnPayNow.isEnabled = false

        lifecycleScope.launch {
            try {
                val timeSdf = SimpleDateFormat("HH:mm:00", Locale.US)
                val formattedTime = timeSdf.format(selectedCalendar.time)

                val request = CheckoutRequest(
                    amount = totalAmount,
                    serviceId = service!!.id,
                    quantity = quantity,
                    serviceAddress = address,
                    scheduledDate = date,
                    scheduledTime = formattedTime
                )

                val response = RetrofitClient.instance.checkout(request)
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.success == true) {
                    val paymongoData = response.body()?.data
                    val data = paymongoData?.get("data") as? Map<*, *>
                    val attributes = data?.get("attributes") as? Map<*, *>
                    val checkoutUrl = attributes?.get("checkout_url") as? String

                    if (!checkoutUrl.isNullOrEmpty()) {
                        isPaymentStarted = true
                        
                        // Open PayMongo using Chrome Custom Tabs
                        val customTabsIntent = androidx.browser.customtabs.CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .build()
                        customTabsIntent.launchUrl(this@BookingPaymentActivity, Uri.parse(checkoutUrl))
                    }
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(this@BookingPaymentActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    binding.btnPayNow.isEnabled = true
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                if (e !is kotlinx.coroutines.CancellationException && e.message?.contains("cancelled", true) != true) {
                    binding.btnPayNow.isEnabled = true
                    Toast.makeText(this@BookingPaymentActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
