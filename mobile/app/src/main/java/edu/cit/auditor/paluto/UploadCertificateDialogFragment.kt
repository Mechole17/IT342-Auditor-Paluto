package edu.cit.auditor.paluto

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import edu.cit.auditor.paluto.api.RetrofitClient
import edu.cit.auditor.paluto.databinding.DialogUploadCertificateBinding
import edu.cit.auditor.paluto.utils.NetworkUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadCertificateDialogFragment : DialogFragment() {

    private var _binding: DialogUploadCertificateBinding? = null
    private val binding get() = _binding!!
    private var selectedFileUri: Uri? = null
    private var onUploaded: (() -> Unit)? = null

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedFileUri = it
            binding.tvFileName.text = it.lastPathSegment ?: "File selected"
            binding.ivFileIcon.setImageResource(android.R.drawable.ic_menu_save)
        }
    }

    companion object {
        fun newInstance(onUploaded: () -> Unit): UploadCertificateDialogFragment {
            val fragment = UploadCertificateDialogFragment()
            fragment.onUploaded = onUploaded
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogUploadCertificateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnClose.setOnClickListener { dismiss() }
        binding.cvFileUpload.setOnClickListener { filePickerLauncher.launch("*/*") }
        binding.btnSubmit.setOnClickListener { submitForm() }
    }

    private fun submitForm() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.etCertTitle.text.toString().trim()

        if (title.isEmpty() || selectedFileUri == null) {
            Toast.makeText(context, "Please provide a title and select a file", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Uploading..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Upload file
                val file = uriToFile(selectedFileUri!!)
                val mimeType = requireContext().contentResolver.getType(selectedFileUri!!) ?: "application/octet-stream"
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val uploadRes = RetrofitClient.instance.uploadCertificateFile(body)

                if (uploadRes.isSuccessful && uploadRes.body() != null) {
                    val fileUrl = uploadRes.body()!!.url
                    
                    // 2. Submit certificate info
                    val request = mapOf("title" to title, "fileUrl" to fileUrl)
                    val response = RetrofitClient.instance.uploadCertificate(request)

                    if (response.isSuccessful) {
                        Toast.makeText(context, "Certificate submitted for review", Toast.LENGTH_SHORT).show()
                        onUploaded?.invoke()
                        dismiss()
                    } else {
                        val errorMsg = NetworkUtils.parseError(response)
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorSnippet = uploadRes.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "File upload failed: $errorSnippet", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Submit for Review"
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val extension = requireContext().contentResolver.getType(uri)?.split("/")?.last() ?: "pdf"
        val file = File(requireContext().cacheDir, "upload_cert_${System.currentTimeMillis()}.$extension")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
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
