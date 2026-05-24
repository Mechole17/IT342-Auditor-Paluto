package edu.cit.auditor.paluto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.auditor.paluto.R
import edu.cit.auditor.paluto.databinding.ItemCookBinding
import edu.cit.auditor.paluto.dto.CookResponse

class CookAdapter(private var cooks: List<CookResponse>) : RecyclerView.Adapter<CookAdapter.CookViewHolder>() {

    class CookViewHolder(val binding: ItemCookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookViewHolder {
        val binding = ItemCookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CookViewHolder, position: Int) {
        val cook = cooks[position]
        holder.binding.apply {
            tvCookName.text = "${cook.firstname} ${cook.lastname}"
            tvExperience.text = "${cook.yearsXp} yrs"
            tvRating.text = String.format("%.1f", cook.averageRating)
            tvBio.text = "Bio: ${cook.bio}"
            tvPrice.text = "Php ${String.format("%.0f", cook.hourlyRate)}"
            
            // Set Initials
            val initials = "${cook.firstname.take(1)}${cook.lastname.take(1)}".uppercase()
            tvInitials.text = initials
            
            btnViewMenu.setOnClickListener {
                val intent = android.content.Intent(holder.itemView.context, edu.cit.auditor.paluto.CookProfileActivity::class.java)
                intent.putExtra("COOK_ID", cook.id)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = cooks.size

    fun updateCooks(newCooks: List<CookResponse>) {
        cooks = newCooks
        notifyDataSetChanged()
    }
}
