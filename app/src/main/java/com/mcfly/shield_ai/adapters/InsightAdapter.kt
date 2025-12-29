package com.mcfly.shield_ai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.network.GuardianInsight
import java.time.format.DateTimeFormatter
import java.util.Locale

class InsightAdapter(private val insights: List<GuardianInsight> = emptyList()) :
    RecyclerView.Adapter<InsightAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelText: TextView = view.findViewById(R.id.labelText)
        val confidenceText: TextView = view.findViewById(R.id.confidenceText)
        val timestampText: TextView = view.findViewById(R.id.timestampText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val patternText: TextView = view.findViewById(R.id.patternText)
        val sourceTypeText: TextView = view.findViewById(R.id.sourceTypeText)
        val influenceTypeText: TextView = view.findViewById(R.id.influenceTypeText)
        val interruptionLevelText: TextView = view.findViewById(R.id.interruptionLevelText)
        val eventTriggerText: TextView = view.findViewById(R.id.eventTriggerText)
        val outcomeText: TextView = view.findViewById(R.id.outcomeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.insight_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val insight = insights[position]
        holder.labelText.text = "Label: ${insight.label}"
        holder.confidenceText.text = "Confidence: %.0f%%".format(insight.confidence * 100)
        holder.timestampText.text = "Time: ${
            DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale.getDefault())
                .format(insight.timestamp)
        }"
        holder.categoryText.text = "Category: ${insight.category}"
        holder.patternText.text = "Pattern: ${insight.patternName ?: "N/A"}"
        holder.sourceTypeText.text = "Source: ${getSourceType(insight.category, insight.patternName)}"
        holder.influenceTypeText.text = "Influence: ${insight.influenceType}"
        holder.interruptionLevelText.text = "Interruption: ${insight.interruptionLevel}"
        holder.eventTriggerText.text = "Trigger: ${insight.trigger}"
        holder.outcomeText.text = "Outcome: ${insight.outcome}"

        // Optional tooltip listeners
        val context = holder.itemView.context
        holder.influenceTypeText.setOnClickListener {
            showTooltip(context, "InfluenceType: How this may be influencing your behavior.")
        }
        holder.interruptionLevelText.setOnClickListener {
            showTooltip(context, "InterruptionLevel: Degree to which your attention was disrupted.")
        }
        holder.eventTriggerText.setOnClickListener {
            showTooltip(context, "Trigger: What event initiated this log.")
        }
        holder.outcomeText.setOnClickListener {
            showTooltip(context, "Outcome: How you responded or what happened.")
        }
    }

    override fun getItemCount(): Int = insights.size

    private fun getSourceType(category: String, patternName: String?): String {
        return when {
            patternName?.contains("ad", ignoreCase = true) == true -> "Ad"
            patternName?.contains("notif", ignoreCase = true) == true -> "Notification"
            category.contains("app", ignoreCase = true) -> "App"
            else -> "Unknown"
        }
    }

    private fun showTooltip(context: android.content.Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
