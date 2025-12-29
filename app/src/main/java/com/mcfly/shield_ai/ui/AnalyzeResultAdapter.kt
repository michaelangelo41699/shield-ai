package com.mcfly.shield_ai.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.network.GuardianInsight

class AnalyzeResultAdapter : RecyclerView.Adapter<AnalyzeResultAdapter.ResultViewHolder>() {

    private val items = mutableListOf<GuardianInsight>()

    fun updateResults(newResults: List<GuardianInsight>) {
        items.clear()
        items.addAll(newResults)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analysis_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = items[position]
        holder.labelText.text = result.label
        holder.confidenceText.text = "Confidence: ${"%.2f".format(result.confidence)}"
        holder.inputText.text = ""  // Optional: You can hide or remove this if unused
    }

    override fun getItemCount(): Int = items.size

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelText: TextView = view.findViewById(R.id.labelText)
        val confidenceText: TextView = view.findViewById(R.id.confidenceText)
        val inputText: TextView = view.findViewById(R.id.inputText)
    }
}
