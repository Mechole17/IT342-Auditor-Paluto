package edu.cit.auditor.paluto.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import coil.load
import edu.cit.auditor.paluto.databinding.DialogManageServiceBinding
import edu.cit.auditor.paluto.services.ServiceRequest
import edu.cit.auditor.paluto.services.ServiceResponse
import edu.cit.auditor.paluto.infrastructure.api.RetrofitClient
import edu.cit.auditor.paluto.infrastructure.common.NetworkUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ManageServiceDialogFragment : DialogFragment() {

    private var _binding: DialogManageServiceBinding? = null
    private val binding get() = _binding!!
    private var editingService: ServiceResponse? = null
    private var selectedImageUri: Uri? = null
    private var onServiceUpdated: (() -> Unit)? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivPreview.visibility = View.VISIBLE
            binding.llUploadPrompt.visibility = View.GONE
            binding.ivPreview.load(it)
        }
    }

    companion object {
        fun newInstance(service: ServiceResponse?, onUpdated: () -> Unit): ManageServiceDialogFragment {
            val fragment = ManageServiceDialogFragment()
            fragment.editingService = service
            fragment.onServiceUpdated = onUpdated
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogManageServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        binding.btnClose.setOnClickListener { dismiss() }
        binding.cvImageUpload.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.btnPublish.setOnClickListener { submitForm() }
    }

    private fun setupUI() {
        editingService?.let { s ->
            binding.tvTitle.text = "Edit Service"
            binding.btnPublish.text = "Save Changes"
            binding.etServiceTitle.setText(s.title)
            binding.etDescription.setText(s.description)
            binding.etIngredients.setText(s.ingredientsList)
            binding.etCost.setText(s.ingredientsCost.toString())
            binding.etServingSize.setText(s.servingSize.toString())
            binding.etPrepTime.setText(s.estPrepTime.toString())

            if (!s.imageUrl.isNullOrEmpty()) {
                binding.ivPreview.visibility = View.VISIBLE
                binding.llUploadPrompt.visibility = View.GONE
                binding.ivPreview.load(s.imageUrl)
            }
        }
    }

    private fun submitForm() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.etServiceTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val ing = binding.etIngredients.text.toString().trim()
        val cost = binding.etCost.text.toString().toDoubleOrNull() ?: 0.0
        val size = binding.etServingSize.text.toString().toIntOrNull() ?: 0
        val time = binding.etPrepTime.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty() || desc.isEmpty() || ing.isEmpty() || cost <= 0 || size <= 0 || time <= 0) {
            Toast.makeText(context, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnPublish.isEnabled = false
        binding.btnPublish.text = "Processing..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                var imageUrl = editingService?.imageUrl ?: ""

                // 1. Handle image upload if a new one was selected
                selectedImageUri?.let { uri ->
                    val file = uriToFile(uri)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    val uploadRes = RetrofitClient.instance.uploadServiceImage(body)
                    if (uploadRes.isSuccessful && uploadRes.body() != null) {
                        imageUrl = uploadRes.body()!!.url
                        Log.d("ManageService", "Image upload successful. URL: $imageUrl")
                    } else {
                        val errorSnippet = uploadRes.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(context, "Image upload failed: $errorSnippet", Toast.LENGTH_LONG).show()
                        binding.btnPublish.isEnabled = true
                        binding.btnPublish.text = if (editingService == null) "Publish Service" else "Save Changes"
                        return@launch
                    }
                }

                // 2. Create or Update Service
                val request = ServiceRequest(title, desc, ing, cost, imageUrl, time, size)
                Log.d("ManageService", "Sending service request with imageUrl: $imageUrl")

                val response = if (editingService == null) {
                    RetrofitClient.instance.createService(request)
                } else {
                    RetrofitClient.instance.updateService(editingService!!.id, request)
                }

                if (response.isSuccessful) {
                    Toast.makeText(context, "Service published!", Toast.LENGTH_SHORT).show()
                    onServiceUpdated?.invoke()
                    dismiss()
                } else {
                    val errorMsg = NetworkUtils.parseError(response)
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnPublish.isEnabled = true
                binding.btnPublish.text = if (editingService == null) "Publish Service" else "Save Changes"
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val file = File(requireContext().cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        // Compress the image to 80% quality to reduce file size for Supabase
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        outputStream.flush()
        outputStream.close()
        inputStream?.close()

        return file
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