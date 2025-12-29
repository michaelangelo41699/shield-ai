package com.mcfly.shield_ai.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.mcfly.shield_ai.R

class TrendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trend)

        val chart = findViewById<BarChart>(R.id.trendChart)
        val textView = findViewById<TextView>(R.id.trendTitle)

        val prefs = getSharedPreferences("ShieldAI", MODE_PRIVATE)
        val trendData = prefs.getStringSet("trend_data", emptySet()) ?: emptySet()

        if (trendData.isEmpty()) {
            textView.text = "No trend data available yet."
            return
        }

        val labels = mutableListOf<String>()
        val series = mutableMapOf<String, MutableList<Float>>() // "Dopamine Spike" → [3, 4, 2...]

        for ((i, entry) in trendData.sorted().withIndex()) {
            val parts = entry.split("|")
            if (parts.size < 2) continue
            val date = parts[0]
            labels.add(date)

            val effects = parts[1].split(",")
            for (effectEntry in effects) {
                val partsEffect = effectEntry.split("=")
                if (partsEffect.size != 2) continue

                val effect = partsEffect[0]
                val count = partsEffect[1].toFloatOrNull() ?: continue
                val list = series.getOrPut(effect) { MutableList(trendData.size) { 0f } }
                list[i] = count
            }
        }

        val dataSets = series.entries.mapIndexed { index, (effect, values) ->
            val entries = values.mapIndexed { i, value -> BarEntry(i.toFloat(), value) }
            BarDataSet(entries, effect).apply {
                color = ColorTemplate.MATERIAL_COLORS[index % ColorTemplate.MATERIAL_COLORS.size]
            } as IBarDataSet
        }

        chart.data = BarData(dataSets)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.granularity = 1f
        chart.axisRight.isEnabled = false
        chart.description = Description().apply { text = "Daily Notification Trends" }
        chart.invalidate()
    }
}
