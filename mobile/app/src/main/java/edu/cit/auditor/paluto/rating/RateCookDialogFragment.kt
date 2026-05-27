package edu.cit.auditor.paluto.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import edu.cit.auditor.paluto.databinding.DialogRateCookBinding
import edu.cit.auditor.paluto.rating.RatingRequest
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.launch

class RateCookDialogFragment : DialogFragment() {

    private var _binding: DialogRateCookBinding? = null
    private val binding get() = _binding!!
    private var bookingId: Long = -1
    private var cookName: String? = null
    var onRatingSubmitted: (() -> Unit)? = null

    companion object {
        fun newInstance(bookingId: Long, cookName: String?, onSubmitted: () -> Unit): RateCookDialogFragment {
            val args = Bundle().apply {
                putLong("booking_id", bookingId)
                putString("cook_name", cookName)
            }
            return RateCookDialogFragment().apply {
                arguments = args
                onRatingSubmitted = onSubmitted
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingId = arguments?.getLong("booking_id") ?: -1
        cookName = arguments?.getString("cook_name")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogRateCookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCookName.text = "Cook $cookName"
        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnSubmitRating.setOnClickListener {
            submitRating()
        }
    }

    private fun submitRating() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = binding.ratingBar.rating.toInt()
        val comment = binding.etComment.text.toString()

        if (rating == 0) {
            Toast.makeText(context, "Please select a star rating", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmitRating.isEnabled = false
        binding.btnSubmitRating.text = "Submitting..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val request = RatingRequest(
                    bookingId = bookingId,
                    rating = rating,
                    comment = comment
                )
                val response = RetrofitClient.instance.submitRating(request)
                if (response.isSuccessful && (response.body()?.success == true || response.code() == 200)) {
                    Toast.makeText(context, "Rating submitted successfully!", Toast.LENGTH_SHORT).show()
                    onRatingSubmitted?.invoke()
                    dismiss()
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    binding.btnSubmitRating.isEnabled = true
                    binding.btnSubmitRating.text = "Submit Rating"
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnSubmitRating.isEnabled = true
                binding.btnSubmitRating.text = "Submit Rating"
            }
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