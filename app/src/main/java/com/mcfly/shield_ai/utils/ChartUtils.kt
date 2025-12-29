package com.mcfly.shield_ai.utils

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

object ChartUtils {
    fun showPieChart(chart: PieChart, data: Map<String, Int>) {
        val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet = PieDataSet(entries, "Psychological Effects").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
        }

        chart.data = PieData(pieDataSet)
        chart.setUsePercentValues(true)
        chart.description = Description().apply { text = "" }
        chart.invalidate()
    }

    fun showBarChart(chart: BarChart, data: Map<String, Int>) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val labels = data.keys.toList()

        val barDataSet = BarDataSet(entries, "Psychological Effects").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
        }

        chart.data = BarData(barDataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.granularity = 1f
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.description = Description().apply { text = "" }
        chart.invalidate()
    }
}
