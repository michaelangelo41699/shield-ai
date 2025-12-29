package com.mcfly.shield_ai.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.data.local.entities.ReflectionEntity
import com.mcfly.shield_ai.ui.adapters.ReflectionAdapter
import com.mcfly.shield_ai.viewmodel.ReflectionViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReflectionActivity : AppCompatActivity() {

    private val viewModel: ReflectionViewModel by viewModels()

    private lateinit var inputField: EditText
    private lateinit var tagSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var historyList: RecyclerView
    private lateinit var fabBack: FloatingActionButton
    private lateinit var editBanner: TextView
    private lateinit var summaryText: TextView

    private lateinit var adapter: ReflectionAdapter
    private var editingReflection: ReflectionEntity? = null
    private var lastDeleted: ReflectionEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reflection)

        inputField = findViewById(R.id.reflectionInput)
        tagSpinner = findViewById(R.id.reflectionTagSpinner)
        saveButton = findViewById(R.id.saveReflectionButton)
        historyList = findViewById(R.id.reflectionHistoryRecycler)
        fabBack = findViewById(R.id.fabBackToMain)
        editBanner = findViewById(R.id.editBanner)
        summaryText = findViewById(R.id.reflectionSummaryText)

        adapter = ReflectionAdapter(
            onItemClick = { reflection ->
                editingReflection = reflection
                inputField.setText(reflection.text)
                val tagPos = (tagSpinner.adapter as ArrayAdapter<String>).getPosition(reflection.tag)
                tagSpinner.setSelection(tagPos)
                editBanner.visibility = View.VISIBLE
                adapter.updateSelected(reflection)
                Toast.makeText(this, "Edit mode activated ✍️", Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = { reflection ->
                adapter.updateSelected(reflection)
                showReflectionOptions(reflection)
            }
        )

        historyList.layoutManager = LinearLayoutManager(this)
        historyList.adapter = adapter

        viewModel.reflections.observe(this) { reflections ->
            adapter.submitList(reflections.reversed())
            updateSummary(reflections)
        }

        saveButton.setOnClickListener {
            val text = inputField.text.toString().trim()
            val tag = tagSpinner.selectedItem?.toString() ?: ""
            if (text.isNotBlank()) {
                val reflection = editingReflection?.copy(text = text, tag = tag)
                    ?: ReflectionEntity(text, tag, System.currentTimeMillis())

                lifecycleScope.launch {
                    viewModel.insertReflection(reflection)
                    inputField.text.clear()
                    tagSpinner.setSelection(0)
                    editingReflection = null
                    editBanner.visibility = View.GONE
                    adapter.updateSelected(null)
                    Toast.makeText(this@ReflectionActivity, "Saved ✍️", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Reflection is empty.", Toast.LENGTH_SHORT).show()
            }
        }

        fabBack.setOnClickListener { finish() }
        fabBack.setOnLongClickListener {
            showPopupMenu()
            true
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val reflection = adapter.currentList[vh.adapterPosition]
                lastDeleted = reflection
                lifecycleScope.launch { viewModel.delete(reflection) }
                Snackbar.make(historyList, "Deleted 🗑️", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        lastDeleted?.let {
                            lifecycleScope.launch { viewModel.insertReflection(it) }
                        }
                    }.show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(historyList)
    }

    private fun showReflectionOptions(reflection: ReflectionEntity) {
        val options = arrayOf("Edit", "Delete", "Export", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Reflection Options")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        editingReflection = reflection
                        inputField.setText(reflection.text)
                        val tagPos = (tagSpinner.adapter as ArrayAdapter<String>).getPosition(reflection.tag)
                        tagSpinner.setSelection(tagPos)
                        editBanner.visibility = View.VISIBLE
                        Toast.makeText(this, "Edit mode activated ✍️", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        lifecycleScope.launch { viewModel.delete(reflection) }
                        Toast.makeText(this, "Deleted 🗑️", Toast.LENGTH_SHORT).show()
                    }
                    2 -> exportSingleReflection(reflection)
                    else -> dialog.dismiss()
                }
            }.show()
    }

    private fun exportSingleReflection(reflection: ReflectionEntity) {
        val exportFile = File(filesDir, "reflection_${reflection.timestamp}.txt")
        val date = SimpleDateFormat("MMM dd, yyyy – hh:mm a", Locale.getDefault()).format(Date(reflection.timestamp))
        exportFile.writeText("[$date] (${reflection.tag}):\n${reflection.text}")

        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", exportFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Reflection"))
    }

    private fun showPopupMenu() {
        val options = arrayOf("Clear All Reflections", "Export to TXT")
        AlertDialog.Builder(this)
            .setTitle("Reflection Options")
            .setItems(options) { _, index ->
                when (index) {
                    0 -> confirmClearAll()
                    1 -> exportReflectionsToText()
                }
            }
            .show()
    }

    private fun confirmClearAll() {
        AlertDialog.Builder(this)
            .setTitle("Delete All?")
            .setMessage("Are you sure you want to delete all reflections?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    viewModel.clearAll()
                    Toast.makeText(this@ReflectionActivity, "All reflections cleared.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun exportReflectionsToText() {
        val reflections = adapter.currentList
        if (reflections.isEmpty()) {
            Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val exportFile = File(filesDir, "reflections_export.txt")
        val content = reflections.joinToString("\n\n") {
            val date = SimpleDateFormat("MMM dd, yyyy – hh:mm a", Locale.getDefault()).format(Date(it.timestamp))
            "[$date] (${it.tag}):\n${it.text}"
        }

        exportFile.writeText(content)

        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", exportFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Reflections"))
    }

    private fun updateSummary(reflections: List<ReflectionEntity>) {
        val count = reflections.size
        val wordCount = reflections.sumOf { it.text.split("\\s+".toRegex()).size }
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        val dateRange = if (reflections.isNotEmpty()) {
            val start = formatter.format(Date(reflections.minOf { it.timestamp }))
            val end = formatter.format(Date(reflections.maxOf { it.timestamp }))
            "From $start to $end"
        } else "No entries yet"

        summaryText.text = "Entries: $count | Words: $wordCount | $dateRange"
    }
}
