package com.mcfly.shield_ai.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.data.local.repository.GoalProfileRepository
import com.mcfly.shield_ai.service.MonitoringService
import com.mcfly.shield_ai.sync.GuardianLogger
import com.mcfly.shield_ai.ui.GoalProfileActivity
import com.mcfly.shield_ai.ui.InsightActivity
import com.mcfly.shield_ai.utils.OnboardingActivity
import com.mcfly.shield_ai.ui.PsychologicalProfileActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var repository: GoalProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // ✅ Initialize Repository Singleton
        repository = GoalProfileRepository.getInstance(this)

        // ✅ Initialize GuardianLogger
        GuardianLogger.initialize(applicationContext)

        // ✅ Request Permissions if Needed
        if (!hasUsageStatsPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        if (!isNotificationAccessEnabled()) {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        // ✅ Start MonitoringService SAFELY after UI is ready
        lifecycleScope.launch {
            delay(500) // Let UI finish loading to avoid foreground crash
            if (hasUsageStatsPermission()) {
                val intent = Intent(this@MainActivity, MonitoringService::class.java)
                ContextCompat.startForegroundService(this@MainActivity, intent)
            }
        }

        // ✅ UI Elements
        val summaryIdealSelf = findViewById<TextView>(R.id.summaryIdealSelf)
        val summaryTopGoal = findViewById<TextView>(R.id.summaryTopGoal)
        val summaryConfidence = findViewById<TextView>(R.id.summaryConfidence)

        val psychProfileButton = findViewById<Button>(R.id.psychProfileButton)
        val goalProfileButton = findViewById<Button>(R.id.goalProfileButton)
        val insightButton = findViewById<Button>(R.id.insightButton)

        // ✅ Load Profile
        lifecycleScope.launch {
            val profile = repository.getProfile("default") // ✅ matches onboarding
            if (profile == null || profile.idealSelf.isBlank()) {
                Toast.makeText(this@MainActivity, "Please complete onboarding", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish()
                return@launch
            }

            summaryIdealSelf.text = "Ideal Self: ${profile.idealSelf}"
            summaryTopGoal.text = "Top Goal: ${profile.goals.firstOrNull() ?: "None set"}"
            summaryConfidence.text = "Confidence Focus: ${profile.idealConfidence}"
        }

        // ✅ Navigation
        psychProfileButton.setOnClickListener {
            startActivity(Intent(this, PsychologicalProfileActivity::class.java))
        }

        goalProfileButton.setOnClickListener {
            startActivity(Intent(this, GoalProfileActivity::class.java))
        }

        insightButton.setOnClickListener {
            startActivity(Intent(this, InsightActivity::class.java))
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            "android:get_usage_stats",
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isNotificationAccessEnabled(): Boolean {
        val enabledPackages = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabledPackages.contains(packageName)
    }
}
