package com.mcfly.shield_ai.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.network.AnalyzeRequest
import com.mcfly.shield_ai.network.LogEntry
import com.mcfly.shield_ai.network.RetrofitClient
import com.mcfly.shield_ai.adapters.GuardianAdapter
import kotlinx.coroutines.launch

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dummyLogs = try {
            listOf(
                LogEntry.create(
                    emotionalState = "Anxious",
                    packageName = "com.socialmedia.app",
                    additionalContext = mapOf(
                        "content" to "Scary world news and unrealistic comparisons",
                        "source" to "dummy_data"
                    )
                )
            )
        } catch (e: IllegalArgumentException) {
            Log.e("AnalysisActivity", "Invalid log entry parameters", e)
            emptyText.text = "Error: Invalid sample data configuration"
            return
        }

        val request = AnalyzeRequest(logs = dummyLogs)
        emptyText.text = "Analyzing your digital patterns..."

        // 🔁 Use coroutine to call suspend function
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.analyzeLogs(request)

                if (response.isSuccessful) {
                    val predictions = response.body()?.results ?: emptyList()
                    Log.d("GuardianAI", "Received ${predictions.size} predictions")

                    if (predictions.isNotEmpty()) {
                        recyclerView.adapter = GuardianAdapter { prediction ->
                            // Handle prediction item click (optional)
                        }.apply {
                            submitList(predictions)
                        }
                        emptyText.text = ""
                    } else {
                        recyclerView.adapter = null
                        emptyText.text = getString(R.string.no_insights_found)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("GuardianAI", "API error ${response.code()}: $errorMsg")
                    emptyText.text = getString(R.string.api_error_message, response.code())
                }
            } catch (e: Exception) {
                Log.e("GuardianAI", "Request failed", e)
                emptyText.text = getString(
                    R.string.network_error_message,
                    e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }
}
