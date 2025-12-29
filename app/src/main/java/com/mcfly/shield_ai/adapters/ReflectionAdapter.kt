package com.mcfly.shield_ai.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity
import java.text.SimpleDateFormat
import java.util.*

class ReflectionAdapter(
    private val onItemClick: (ReflectionEntity) -> Unit,
    private val onItemLongClick: (ReflectionEntity) -> Unit,
    private var selectedReflection: ReflectionEntity? = null
) : ListAdapter<ReflectionEntity, ReflectionAdapter.ReflectionViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ReflectionEntity>() {
        override fun areItemsTheSame(oldItem: ReflectionEntity, newItem: ReflectionEntity): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: ReflectionEntity, newItem: ReflectionEntity): Boolean {
            return oldItem == newItem
        }
    }

    fun updateSelected(reflection: ReflectionEntity?) {
        selectedReflection = reflection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reflection_item, parent, false)
        return ReflectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReflectionViewHolder, position: Int) {
        val reflection = getItem(position)
        holder.bind(reflection)
    }

    inner class ReflectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.reflectionText)
        private val tagView: TextView = itemView.findViewById(R.id.reflectionTag)
        private val timestampView: TextView = itemView.findViewById(R.id.reflectionTimestamp)
        private val emojiView: TextView = itemView.findViewById(R.id.reflectionEmoji)

        fun bind(reflection: ReflectionEntity) {
            textView.text = reflection.text
            tagView.text = reflection.tag.ifBlank { "No tag" }

            val formatter = SimpleDateFormat("MMM dd, yyyy – hh:mm a", Locale.getDefault())
            timestampView.text = formatter.format(Date(reflection.timestamp))

            val moodEmoji = when (reflection.tag.lowercase()) {
                "grateful" -> "🙏"
                "anxious" -> "😰"
                "hopeful" -> "🌈"
                "tired" -> "😴"
                "happy" -> "😊"
                "angry" -> "😡"
                "sad" -> "😢"
                else -> "📝"
            }
            emojiView.text = moodEmoji

            // Highlight if selected
            itemView.setBackgroundColor(
                if (reflection == selectedReflection) Color.parseColor("#e0f7fa") else Color.WHITE
            )

            itemView.setOnClickListener {
                onItemClick(reflection)
            }

            itemView.setOnLongClickListener {
                onItemLongClick(reflection)
                true
            }
        }
    }
}
