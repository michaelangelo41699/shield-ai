package com.mcfly.shield_ai.adapters

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.logic.AppClassification
import com.mcfly.shield_ai.network.GuardianInsight
import java.text.SimpleDateFormat
import java.util.*

class GuardianAdapter(
    private val onItemClick: (GuardianInsight) -> Unit
) : RecyclerView.Adapter<GuardianAdapter.ViewHolder>() {

    private var insights: List<GuardianInsight> = emptyList()
    private var expandedPosition: Int? = null

    fun submitList(newInsights: List<GuardianInsight>) {
        insights = newInsights
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val labelText: TextView = view.findViewById(R.id.labelText)
        private val confidenceText: TextView = view.findViewById(R.id.confidenceText)
        private val appTypeText: TextView = view.findViewById(R.id.appTypeText)
        private val categoryText: TextView = view.findViewById(R.id.categoryText)
        private val alignmentBar: View = view.findViewById(R.id.alignmentBar)

        private val expansionLayout: LinearLayout = view.findViewById(R.id.insightExpansionLayout)
        private val patternText: TextView = view.findViewById(R.id.patternText)
        private val sourceText: TextView = view.findViewById(R.id.sourceText)
        private val misalignmentText: TextView = view.findViewById(R.id.misalignmentText)
        private val timestampText: TextView = view.findViewById(R.id.timestampText)

        private val refocusButton: Button = view.findViewById(R.id.refocusButton)
        private val swapAppButton: Button = view.findViewById(R.id.swapAppButton)
        private val reminderButton: Button = view.findViewById(R.id.reminderButton)

        init {
            itemView.setOnClickListener {
                val currentPos = adapterPosition
                expandedPosition = if (expandedPosition == currentPos) null else currentPos
                notifyDataSetChanged()
                onItemClick(insights[currentPos])
            }
        }

        fun bind(insight: GuardianInsight) {
            val context = itemView.context

            // Main summary
            labelText.text = "Label: ${insight.label}"
            confidenceText.text = "Confidence: ${"%.2f".format(insight.confidence)}"

            val packageName = insight.metadata["packageName"] ?: "unknown"
            val classification = AppClassification.getAppClassification(packageName)

            if (classification != null) {
                appTypeText.text = "App Type: ${classification.label}"
                categoryText.text = "Category: ${classification.category}"
            } else {
                appTypeText.text = "App Type: Unclassified"
                categoryText.text = "Category: Unknown"
            }

            // Alignment bar color logic
            val drift = insight.metadata["goalMisalignment"] ?: ""
            alignmentBar.setBackgroundColor(
                when {
                    drift.contains("severe", true) -> context.getColor(R.color.red)
                    drift.isNotEmpty() -> context.getColor(R.color.yellow)
                    else -> context.getColor(R.color.green)
                }
            )

            // Expansion section visibility
            val isExpanded = expandedPosition == adapterPosition
            expansionLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Populate expansion details
            patternText.text = "Pattern: ${insight.patternName ?: "N/A"}"
            sourceText.text = "Source: ${insight.sourceType ?: "Unknown"}"
            misalignmentText.text = if (drift.isNotEmpty()) "Misalignment: $drift" else "Aligned"

            val formatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
            timestampText.text = "Time: ${formatter.format(Date(insight.timestamp))}"

            // Quick-Action Buttons
            refocusButton.setOnClickListener {
                launchApp(context, "cc.forestapp")
            }

            swapAppButton.setOnClickListener {
                launchApp(context, "com.duolingo")
            }

            reminderButton.setOnClickListener {
                setReminderIn1Hour(context)
            }
        }

        private fun launchApp(context: Context, packageName: String) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "App not found: $packageName", Toast.LENGTH_SHORT).show()
            }
        }

        private fun setReminderIn1Hour(context: Context) {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "Check your goals")
                putExtra(AlarmClock.EXTRA_HOUR, (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1) % 24)
                putExtra(AlarmClock.EXTRA_MINUTES, Calendar.getInstance().get(Calendar.MINUTE))
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guardian_insight, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(insights[position])
    }

    override fun getItemCount() = insights.size
}
