package com.mcfly.shield_ai.ui

import android.app.AppOpsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.adapters.GuardianAdapter
import com.mcfly.shield_ai.data.local.entities.GuardianInsightEntity
import com.mcfly.shield_ai.logic.AppClassification
import com.mcfly.shield_ai.model.GoalProfileViewModel
import com.mcfly.shield_ai.network.AnalyzeRequest
import com.mcfly.shield_ai.network.GuardianInsight
import com.mcfly.shield_ai.network.RetrofitClient
import com.mcfly.shield_ai.service.LiveUsageSnapshotter
import com.mcfly.shield_ai.sync.GuardianLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import java.util.*

class InsightActivity : AppCompatActivity() {

    private val viewModel: GoalProfileViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var timeRangeSelector: Spinner
    private lateinit var emptyText: TextView

    private lateinit var summaryCard: View
    private lateinit var summaryStatusText: TextView
    private lateinit var summaryCategoryText: TextView
    private lateinit var summaryTimeRangeText: TextView

    private lateinit var chartContainer: LinearLayout
    private lateinit var fab: FloatingActionButton

    private var selectedTimeRangeLabel: String = "Today"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight)

        recyclerView = findViewById(R.id.recyclerView)
        timeRangeSelector = findViewById(R.id.timeRangeSelector)
        emptyText = findViewById(R.id.emptyText)
        chartContainer = findViewById(R.id.chartContainer)
        fab = findViewById(R.id.fabAction)

        summaryCard = findViewById(R.id.alignmentSummaryCard)
        summaryStatusText = findViewById(R.id.summaryStatusText)
        summaryCategoryText = findViewById(R.id.summaryCategoryText)
        summaryTimeRangeText = findViewById(R.id.summaryTimeRangeText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val currentDriftStatus = summaryStatusText.text.toString().lowercase()
            when {
                currentDriftStatus.contains("red") || currentDriftStatus.contains("high") -> {
                    launchApp("com.forestapp")
                    Toast.makeText(this, "Launching refocus tool…", Toast.LENGTH_SHORT).show()
                }
                currentDriftStatus.contains("yellow") || currentDriftStatus.contains("moderate") -> {
                    setReminderInOneHour("Recenter session: How do you feel?")
                }
                else -> {
                    val intent = Intent(this, ReflectionActivity::class.java)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Reflection activity not available yet.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.time_ranges,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeRangeSelector.adapter = adapter

        viewModel.alignmentSummary.observe(this) { summary ->
            summaryCard.visibility = View.VISIBLE
            summaryStatusText.text = "Status: ${summary.status}"
            summaryCategoryText.text = summary.topDriftedCategory?.let {
                "Top Drifted Category: $it"
            } ?: "No significant drift detected"
            summaryTimeRangeText.text = "Range: ${summary.timeRange}"
        }

        lifecycleScope.launch {
            val dailyInsights = GuardianLogger.getInstance().getInsightsByDay(3)
            val flatList = dailyInsights.values.flatten()
            populateMiniDriftChart(flatList, chartContainer)
        }

        timeRangeSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTimeRangeLabel = when (position) {
                    0 -> "Today"
                    1 -> "Last 3 Days"
                    2 -> "Last 7 Days"
                    else -> "Today"
                }

                if (!hasUsageStatsPermission()) {
                    promptForUsageStatsPermission()
                } else {
                    fetchAndAnalyzeData(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun populateMiniDriftChart(insights: List<GuardianInsight>, container: LinearLayout) {
        val grouped = insights.groupBy {
            it.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSortedMap()

        container.removeAllViews()

        grouped.entries.takeLast(3).forEach { (date, dailyInsights) ->
            val statusEmoji = when {
                dailyInsights.any { it.label.contains("escapism", true) || it.category == "drift" } -> "🔴"
                dailyInsights.any { it.label.contains("mild", true) || it.category == "warning" } -> "🟡"
                else -> "🟢"
            }

            val summary = dailyInsights.firstOrNull()?.patternName ?: "No pattern"
            val view = TextView(this).apply {
                text = "$date: $statusEmoji $summary"
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            container.addView(view)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Binder.getCallingUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun promptForUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("To understand your digital patterns and offer deeper insights, Shield-AI needs access to your usage history. You’re in control and can revoke it at any time.")
            .setPositiveButton("Grant Access") { _, _ -> promptForUsageStatsPermission() }
            .setNegativeButton("Maybe Later", null)
            .show()
    }

    private fun fetchAndAnalyzeData(rangeDaysIndex: Int) {
        val days = when (rangeDaysIndex) {
            0 -> 1
            1 -> 3
            2 -> 7
            else -> 1
        }

        val usageLogs = LiveUsageSnapshotter.collect(this, days)

        if (usageLogs.isEmpty()) {
            emptyText.text = getString(R.string.no_usage_data)
            recyclerView.adapter = null
            return
        }

        emptyText.text = getString(R.string.analysis_in_progress)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.analyzeLogs(AnalyzeRequest(usageLogs))
                if (response.isSuccessful) {
                    val predictions = response.body()?.results ?: emptyList()

                    launch(Dispatchers.Main) {
                        if (predictions.isNotEmpty()) {
                            recyclerView.adapter = GuardianAdapter { insight ->
                                val label = insight.metadata["classification_label"] ?: "Unknown"
                                val type = insight.metadata["classification_type"] ?: "-"
                                val category = insight.metadata["classification_category"] ?: "-"
                                val risk = insight.metadata["classification_risk"] ?: "-"
                                Log.d("InsightView", "App: $label, Type: $type, Category: $category, Risk: $risk")
                            }.apply {
                                submitList(predictions)
                            }

                            // 🚨 Guardian Voice Coaching Trigger System
                            val highRiskLabels = listOf(
                                "comparison_dysphoria",
                                "insecurity",
                                "hopelessness",
                                "impulse_overload",
                                "emotional_instability",
                                "gaslighting",
                                "information_fatigue"
                            )

                            predictions.forEach { insight ->
                                val isHighRiskLabel = insight.label in highRiskLabels
                                val isMisaligned = insight.metadata?.goalMisalignment != null
                                val isInterruptive = insight.metadata?.isInterruptive == true
                                val isHighConfidence = (insight.confidence.toFloatOrNull() ?: 0.0f) >= 0.85f

                                val shouldShowPopup = isHighRiskLabel || isMisaligned || isInterruptive || isHighConfidence

                                if (shouldShowPopup) {
                                    CoachPopupManager.showCoachingPopup(this@InsightActivity, insight)
                                    return@forEach // Show only one popup per batch
                                }
                            }

                            emptyText.text = ""

                            val entities = predictions.map { GuardianInsightEntity.from(it) }
                            viewModel.computeAlignmentSummary(entities, selectedTimeRangeLabel)

                        } else {
                            recyclerView.adapter = null
                            emptyText.text = getString(R.string.no_insights_found)
                        }

                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("InsightActivity", "API error: $errorMsg")
                    launch(Dispatchers.Main) {
                        emptyText.text = getString(R.string.api_error_template, "Error", response.code())
                    }
                }
            } catch (e: Exception) {
                Log.e("InsightActivity", "Failed to analyze", e)
                launch(Dispatchers.Main) {
                    emptyText.text = getString(R.string.insight_network_error, e.localizedMessage)
                }
            }
        }
    }

    private fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "App not found: $packageName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setReminderInOneHour(message: String = "Gentle reminder to reflect and recenter.") {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = Uri.parse("content://com.android.calendar/events")
            putExtra(CalendarContract.Events.TITLE, message)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 3600000)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 5400000)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No calendar app found to set reminder.", Toast.LENGTH_SHORT).show()
        }
    }
}
