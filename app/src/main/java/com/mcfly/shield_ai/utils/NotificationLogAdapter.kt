package com.mcfly.shield_ai.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R

class NotificationLogAdapter(private val logs: List<String>) :
    RecyclerView.Adapter<NotificationLogAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logTitle: TextView = view.findViewById(R.id.logTitle)
        val logText: TextView = view.findViewById(R.id.logText)
        val logCategory: TextView = view.findViewById(R.id.logCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_entry, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val parts = logs[position].split("|")
        if (parts.size >= 5) {
            val (_, packageName, title, text, category) = parts
            holder.logTitle.text = "$title — $packageName"
            holder.logText.text = text
            holder.logCategory.text = "Category: $category"
        } else {
            holder.logTitle.text = "Malformed Entry"
            holder.logText.text = logs[position]
            holder.logCategory.text = ""
        }
    }

    override fun getItemCount(): Int = logs.size
}
