package com.mcfly.shield_ai.logic

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object FocusAppManager {
    private const val PREFS_NAME = "shield_focus_prefs"
    private const val KEY_PACKAGE_NAME = "focus_app_package"

    fun launchOrChooseFocusApp(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedPackage = prefs.getString(KEY_PACKAGE_NAME, null)

        if (savedPackage != null) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(savedPackage)
                if (intent != null) {
                    context.startActivity(intent)
                    Toast.makeText(context, "Launching your focus app…", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "App not found. Please choose again.", Toast.LENGTH_SHORT).show()
                    prefs.edit().remove(KEY_PACKAGE_NAME).apply()
                    showAppChooser(context)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error launching app.", Toast.LENGTH_SHORT).show()
            }
        } else {
            showAppChooser(context)
        }
    }

    private fun showAppChooser(context: Context) {
        val focusApps = listOf(
            "com.forestapp", "com.insighttimer", "com.headspace.android", "com.calm.android", "org.mindfulness.everyday"
        )

        val installedApps = focusApps
            .mapNotNull { pkg ->
                val label = try {
                    val appInfo = context.packageManager.getApplicationInfo(pkg, 0)
                    context.packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    null
                }
                if (label != null) pkg to label else null
            }

        if (installedApps.isEmpty()) {
            Toast.makeText(context, "No supported focus apps found. Please install one like Forest or Calm.", Toast.LENGTH_LONG).show()
            return
        }

        val labels = installedApps.map { it.second }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Choose a focus app")
            .setItems(labels) { _, which ->
                val selectedPackage = installedApps[which].first
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_PACKAGE_NAME, selectedPackage)
                    .apply()
                launchOrChooseFocusApp(context) // Launch right after selection
            }
            .show()
    }
}
