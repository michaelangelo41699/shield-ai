package com.mcfly.shield_ai.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.logic.AlignmentResult
import com.mcfly.shield_ai.logic.AlignmentStatus

class AlignmentAdapter(private val items: List<AlignmentResult>) :
    RecyclerView.Adapter<AlignmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.alignmentCard)
        val label: TextView = view.findViewById(R.id.alignmentLabel)
        val status: TextView = view.findViewById(R.id.alignmentStatus)
        val insight: TextView = view.findViewById(R.id.alignmentInsight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alignment_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = items[position]

        holder.label.text = result.needLabel.replaceFirstChar { it.uppercase() }

        // Status and color coding
        when (result.status) {
            AlignmentStatus.ALIGNED -> {
                holder.status.text = "✅ Aligned"
                holder.card.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // light green
            }
            AlignmentStatus.DRIFTING -> {
                holder.status.text = "⚠️ Drift Detected"
                holder.card.setCardBackgroundColor(Color.parseColor("#FFF8E1")) // light yellow
            }
            AlignmentStatus.UNKNOWN -> {
                holder.status.text = "⏳ Learning"
                holder.card.setCardBackgroundColor(Color.parseColor("#ECEFF1")) // light gray
            }
        }

        holder.insight.text = result.insight
    }

    override fun getItemCount(): Int = items.size
}
