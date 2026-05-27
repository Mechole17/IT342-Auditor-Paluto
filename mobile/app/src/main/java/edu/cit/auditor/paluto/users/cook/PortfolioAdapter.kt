package edu.cit.auditor.paluto.users.cook

import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.auditor.paluto.R
import edu.cit.auditor.paluto.databinding.ItemCertificateBinding
import edu.cit.auditor.paluto.databinding.ItemReviewBinding
import edu.cit.auditor.paluto.databinding.ItemServiceBinding
import edu.cit.auditor.paluto.certificate.CertificateResponse
import edu.cit.auditor.paluto.rating.RatingResponse
import edu.cit.auditor.paluto.services.ServiceResponse

sealed class PortfolioItem {
    data class Service(val data: ServiceResponse) : PortfolioItem()
    data class Certificate(val data: CertificateResponse) : PortfolioItem()
    data class Review(val data: RatingResponse) : PortfolioItem()
}

class PortfolioAdapter(
    private val onEditService: (ServiceResponse) -> Unit,
    private val onDeleteService: (ServiceResponse) -> Unit,
    private val onDeleteCertificate: (CertificateResponse) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<PortfolioItem> = emptyList()

    companion object {
        private const val TYPE_SERVICE = 0
        private const val TYPE_CERTIFICATE = 1
        private const val TYPE_REVIEW = 2
    }

    fun setItems(newItems: List<PortfolioItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PortfolioItem.Service -> TYPE_SERVICE
            is PortfolioItem.Certificate -> TYPE_CERTIFICATE
            is PortfolioItem.Review -> TYPE_REVIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SERVICE -> ServiceViewHolder(ItemServiceBinding.inflate(inflater, parent, false))
            TYPE_CERTIFICATE -> CertificateViewHolder(ItemCertificateBinding.inflate(inflater, parent, false))
            TYPE_REVIEW -> ReviewViewHolder(ItemReviewBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ServiceViewHolder -> holder.bind((item as PortfolioItem.Service).data, onEditService, onDeleteService)
            is CertificateViewHolder -> holder.bind((item as PortfolioItem.Certificate).data, onDeleteCertificate)
            is ReviewViewHolder -> holder.bind((item as PortfolioItem.Review).data)
        }
    }

    override fun getItemCount() = items.size

    class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceResponse, onEdit: (ServiceResponse) -> Unit, onDelete: (ServiceResponse) -> Unit) {
            binding.apply {
                tvServiceTitle.text = service.title
                tvServingSize.text = "🍽 Serves ${service.servingSize}"
                tvPrepTime.text = "⏱ ${service.estPrepTime} mins"
                tvPrice.text = "Php ${String.format("%,.0f", service.ingredientsCost)}"
                
                if (!service.imageUrl.isNullOrEmpty()) {
                    ivServiceImage.load(service.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ramen)
                        error(R.drawable.ramen)
                    }
                } else {
                    ivServiceImage.setImageResource(R.drawable.ramen)
                }

                btnViewDetails.text = "Edit"
                btnViewDetails.setOnClickListener { onEdit(service) }
                
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener { onDelete(service) }
            }
        }
    }

    class CertificateViewHolder(private val binding: ItemCertificateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cert: CertificateResponse, onDelete: (CertificateResponse) -> Unit) {
            binding.apply {
                tvCertTitle.text = cert.title
                
                tvViewLink.paintFlags = tvViewLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tvViewLink.setOnClickListener {
                    if (!cert.fileUrl.isNullOrEmpty()) {
                        val customTabsIntent = CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .build()
                        customTabsIntent.launchUrl(itemView.context, Uri.parse(cert.fileUrl))
                    }
                }

                tvRemoveLink.paintFlags = tvRemoveLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tvRemoveLink.setOnClickListener { onDelete(cert) }

                // Status Badge logic
                val (bg, color) = when(cert.status.uppercase()) {
                    "APPROVED" -> 0xFFD1E7DD.toInt() to 0xFF0A3622.toInt()
                    "REJECTED" -> 0xFFF8D7DA.toInt() to 0xFF58151C.toInt()
                    else -> 0xFFFFF3CD.toInt() to 0xFF856404.toInt() // PENDING
                }
                
                tvStatus.text = cert.status.uppercase()
                tvStatus.setTextColor(color)
                tvStatus.backgroundTintList = ColorStateList.valueOf(bg)
            }
        }
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: RatingResponse) {
            binding.apply {
                tvCustomerName.text = review.customerName
                tvStars.text = "★".repeat(review.rating) + "☆".repeat(5 - review.rating)
                tvComment.text = review.comment
            }
        }
    }
}
