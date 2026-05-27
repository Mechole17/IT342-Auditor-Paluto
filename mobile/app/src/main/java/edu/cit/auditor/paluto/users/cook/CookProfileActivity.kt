package edu.cit.auditor.paluto.users.cook

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import edu.cit.auditor.paluto.databinding.ActivityCookProfileBinding
import edu.cit.auditor.paluto.certificate.CertificateResponse
import edu.cit.auditor.paluto.rating.RatingResponse
import edu.cit.auditor.paluto.services.ServiceResponse
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CookProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCookProfileBinding
    private lateinit var contentAdapter: CookProfileContentAdapter
    private var cookId: Long = -1

    private var services: List<ServiceResponse> = emptyList()
    private var reviews: List<RatingResponse> = emptyList()
    private var certificates: List<CertificateResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCookProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cookId = intent.getLongExtra("COOK_ID", -1)
        if (cookId == -1L) {
            Toast.makeText(this, "Cook not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupTabs()
        fetchCookData()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        contentAdapter = CookProfileContentAdapter()
        binding.rvContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(this@CookProfileActivity)
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateContent(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchCookData() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val cookDef = async { RetrofitClient.instance.getCookById(cookId) }
                val servicesDef = async { RetrofitClient.instance.getServicesByCookId(cookId) }
                val certsDef = async { RetrofitClient.instance.getCookCertificates(cookId) }
                val ratingsDef = async { RetrofitClient.instance.getCookRatings(cookId) }
                val avgDef = async { RetrofitClient.instance.getCookAverageRating(cookId) }

                val cookRes = cookDef.await()
                val servicesRes = servicesDef.await()
                val certsRes = certsDef.await()
                val ratingsRes = ratingsDef.await()
                val avgRes = avgDef.await()

                binding.progressBar.visibility = View.GONE

                if (cookRes.isSuccessful) {
                    val cook = cookRes.body()?.data
                    if (cook != null) {
                        displayCookInfo(cook, avgRes.body()?.data ?: 0.0)
                    }
                }

                services = servicesRes.body()?.data ?: emptyList()
                certificates = certsRes.body()?.data?.filter { it.status == "APPROVED" } ?: emptyList()
                reviews = ratingsRes.body()?.data ?: emptyList()

                // Update tab titles with counts
                binding.tabLayout.getTabAt(1)?.text = "Reviews (${reviews.size})"

                // Show default tab content
                updateContent(binding.tabLayout.selectedTabPosition)

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@CookProfileActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCookInfo(cook: CookResponse, avgRating: Double) {
        binding.apply {
            tvCookName.text = "${cook.firstname} ${cook.lastname}"
            tvBio.text = cook.bio
            tvHourlyRate.text = "Php ${String.format("%.0f", cook.hourlyRate)}"
            tvExperience.text = "${cook.yearsXp} years"
            tvRating.text = String.format("%.1f", avgRating)

            val initials = "${cook.firstname.take(1)}${cook.lastname.take(1)}".uppercase()
            tvInitials.text = initials
        }
    }

    private fun updateContent(position: Int) {
        when (position) {
            0 -> contentAdapter.setItems(services.map { CookProfileItem.Service(it) })
            1 -> contentAdapter.setItems(reviews.map { CookProfileItem.Review(it) })
            2 -> contentAdapter.setItems(certificates.map { CookProfileItem.Certificate(it) })
        }
    }
}