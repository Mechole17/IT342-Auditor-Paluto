package edu.cit.auditor.paluto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.auditor.paluto.R
import edu.cit.auditor.paluto.databinding.ItemCertificateBinding
import edu.cit.auditor.paluto.databinding.ItemReviewBinding
import edu.cit.auditor.paluto.databinding.ItemServiceBinding
import edu.cit.auditor.paluto.dto.CertificateResponse
import edu.cit.auditor.paluto.dto.RatingResponse
import edu.cit.auditor.paluto.dto.ServiceResponse

sealed class CookProfileItem {
    data class Service(val data: ServiceResponse) : CookProfileItem()
    data class Review(val data: RatingResponse) : CookProfileItem()
    data class Certificate(val data: CertificateResponse) : CookProfileItem()
}

class CookProfileContentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<CookProfileItem> = emptyList()

    companion object {
        private const val TYPE_SERVICE = 0
        private const val TYPE_REVIEW = 1
        private const val TYPE_CERTIFICATE = 2
    }

    fun setItems(newItems: List<CookProfileItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CookProfileItem.Service -> TYPE_SERVICE
            is CookProfileItem.Review -> TYPE_REVIEW
            is CookProfileItem.Certificate -> TYPE_CERTIFICATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SERVICE -> ServiceViewHolder(ItemServiceBinding.inflate(inflater, parent, false))
            TYPE_REVIEW -> ReviewViewHolder(ItemReviewBinding.inflate(inflater, parent, false))
            TYPE_CERTIFICATE -> CertificateViewHolder(ItemCertificateBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ServiceViewHolder -> holder.bind((item as CookProfileItem.Service).data)
            is ReviewViewHolder -> holder.bind((item as CookProfileItem.Review).data)
            is CertificateViewHolder -> holder.bind((item as CookProfileItem.Certificate).data)
        }
    }

    override fun getItemCount() = items.size

    class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceResponse) {
            binding.apply {
                tvServiceTitle.text = service.title
                tvServingSize.text = "🍽 Serves ${service.servingSize}"
                tvPrepTime.text = "⏱ ${service.estPrepTime} mins prep time"
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

                btnViewDetails.setOnClickListener {
                    val intent = android.content.Intent(itemView.context, edu.cit.auditor.paluto.MealDetailsActivity::class.java)
                    intent.putExtra("SERVICE_ID", service.id)
                    itemView.context.startActivity(intent)
                }
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

    class CertificateViewHolder(private val binding: ItemCertificateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cert: CertificateResponse) {
            binding.apply {
                tvCertTitle.text = cert.title
                tvViewLink.paintFlags = tvViewLink.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                
                tvViewLink.setOnClickListener {
                    if (!cert.fileUrl.isNullOrEmpty()) {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                        intent.data = android.net.Uri.parse(cert.fileUrl)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}
