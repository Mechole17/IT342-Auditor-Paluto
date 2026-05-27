package edu.cit.auditor.paluto.booking

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.cit.auditor.paluto.databinding.ItemTimelineStepBinding

data class TimelineStep(
    val label: String,
    val desc: String,
    val isActive: Boolean,
    val isError: Boolean = false,
    val isLast: Boolean = false
)

class TimelineAdapter(private val steps: List<TimelineStep>) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(val binding: ItemTimelineStepBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val step = steps[position]
        holder.binding.apply {
            tvStepLabel.text = step.label
            tvStepDesc.text = step.desc
            
            // Indicator color
            val color = when {
                step.isActive -> if (step.isError) 0xFFBA1313.toInt() else 0xFF27AE60.toInt()
                else -> 0xFFDDDDDD.toInt()
            }
            indicatorCircle.backgroundTintList = ColorStateList.valueOf(color)
            line.backgroundTintList = ColorStateList.valueOf(color)
            
            tvStepLabel.setTextColor(if (step.isActive) 0xFF000000.toInt() else 0xFFAAAAAA.toInt())
            
            line.visibility = if (step.isLast) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount() = steps.size
}
