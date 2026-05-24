package edu.cit.auditor.paluto

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.ActivityMealDetailsBinding
import edu.cit.auditor.paluto.dto.ServiceResponse
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.launch

class MealDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailsBinding
    private var serviceId: Long = -1
    private var quantity: Int = 1
    private var service: ServiceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceId = intent.getLongExtra("SERVICE_ID", -1)
        if (serviceId == -1L) {
            Toast.makeText(this, "Meal not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchServiceDetails()
        setupQuantityControls()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnBook.setOnClickListener {
            // Handle booking logic or navigate to payment
            Toast.makeText(this, "Booking feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchServiceDetails() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getServiceById(serviceId)
                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body()?.success == true) {
                    service = response.body()?.data
                    service?.let { displayServiceDetails(it) }
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(this@MealDetailsActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@MealDetailsActivity, "Failed to load meal details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayServiceDetails(service: ServiceResponse) {
        binding.apply {
            tvMealTitle.text = service.title
            
            val prepTimeText = if (service.estPrepTime >= 60) {
                val hours = service.estPrepTime / 60
                val mins = service.estPrepTime % 60
                if (mins == 0) "$hours ${if (hours == 1) "hour" else "hours"}"
                else "$hours hr $mins mins"
            } else {
                "${service.estPrepTime} mins"
            }
            tvPrepTimeValue.text = prepTimeText

            tvDescription.text = service.description
            tvIngredients.text = service.ingredientsList
            tvEstCost.text = "Php ${String.format("%,.0f", service.ingredientsCost)}"
            
            if (!service.imageUrl.isNullOrEmpty()) {
                ivMealImage.load(service.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ramen)
                    error(R.drawable.ramen)
                }
            }
        }
    }

    private fun setupQuantityControls() {
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityDisplay()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityDisplay()
        }
    }

    private fun updateQuantityDisplay() {
        binding.tvQuantity.text = quantity.toString()
    }
}
