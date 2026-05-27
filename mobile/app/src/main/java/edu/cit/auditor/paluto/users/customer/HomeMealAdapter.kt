package edu.cit.auditor.paluto.users.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.auditor.paluto.R
import edu.cit.auditor.paluto.databinding.ItemHomeMealBinding
import edu.cit.auditor.paluto.services.ServiceResponse

class HomeMealAdapter(
    private var meals: List<ServiceResponse>,
    private val onClick: (ServiceResponse) -> Unit
) : RecyclerView.Adapter<HomeMealAdapter.MealViewHolder>() {

    class MealViewHolder(val binding: ItemHomeMealBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemHomeMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.binding.apply {
            tvMealTitle.text = meal.title
            tvPrice.text = "₱${String.format("%,.0f", meal.ingredientsCost)} + Labor"

            if (!meal.imageUrl.isNullOrEmpty()) {
                ivMealImage.load(meal.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ramen)
                    error(R.drawable.ramen)
                }
            } else {
                ivMealImage.setImageResource(R.drawable.ramen)
            }

            root.setOnClickListener { onClick(meal) }
        }
    }

    override fun getItemCount() = meals.size

    fun updateMeals(newMeals: List<ServiceResponse>) {
        meals = newMeals
        notifyDataSetChanged()
    }
}