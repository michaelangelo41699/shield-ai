package com.mcfly.shield_ai.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.logic.AlignmentStatus
import com.mcfly.shield_ai.logic.GoalAlignmentAnalyzer
import com.mcfly.shield_ai.sync.GuardianLogger
import kotlinx.coroutines.launch

class GoalProfileActivity : AppCompatActivity() {

    private val viewModel: GoalProfileViewModel by viewModels()

    private lateinit var alignmentBanner: TextView
    private lateinit var categoryDetailText: TextView
    private lateinit var driftSummaryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_profile)

        alignmentBanner = findViewById(R.id.alignmentBanner)
        categoryDetailText = findViewById(R.id.categoryDetailText)
        driftSummaryText = findViewById(R.id.driftSummaryText)

        lifecycleScope.launch {
            viewModel.goalAlignmentStatus.collect { status ->
                updateBanner(status)
            }
        }

        lifecycleScope.launch {
            val recentLogs = GuardianLogger.getInstance().getRecentInsights()
            val currentProfile = viewModel.getProfileOnce()

            if (currentProfile != null && recentLogs.isNotEmpty()) {
                val status = GoalAlignmentAnalyzer.evaluate(currentProfile, recentLogs)
                viewModel.updateAlignmentStatus(AlignmentStatus.valueOf(status.uppercase()))

                val (topCategory, summary) = GoalAlignmentAnalyzer.getDriftDetail(currentProfile, recentLogs)
                runOnUiThread {
                    if (topCategory != null) {
                        categoryDetailText.text = "📌 Top Drifted Goal: $topCategory"
                        driftSummaryText.text = summary
                        categoryDetailText.visibility = View.VISIBLE
                        driftSummaryText.visibility = View.VISIBLE
                    } else {
                        categoryDetailText.text = "🎯 No clear drift detected"
                        categoryDetailText.visibility = View.VISIBLE
                        driftSummaryText.visibility = View.GONE
                    }
                }
            } else {
                alignmentBanner.text = "❔ Not enough data to analyze alignment."
                alignmentBanner.setBackgroundResource(R.color.alignment_gray)
            }
        }
    }

    private fun updateBanner(status: AlignmentStatus) {
        when (status) {
            AlignmentStatus.ALIGNED -> {
                alignmentBanner.text = "✅ You're aligned with your goals."
                alignmentBanner.setBackgroundResource(R.color.alignment_green)
            }
            AlignmentStatus.DRIFTING -> {
                alignmentBanner.text = "⚠️ You're drifting from key goals."
                alignmentBanner.setBackgroundResource(R.color.alignment_yellow)
            }
            AlignmentStatus.UNKNOWN -> {
                alignmentBanner.text = "❔ Alignment unknown. No data."
                alignmentBanner.setBackgroundResource(R.color.alignment_gray)
            }
        }
    }
}
