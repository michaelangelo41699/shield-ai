package com.mcfly.shield_ai.coach

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
// import androidx.compose.ui.semantics.text // <-- 1. REMOVED: This import is for Jetpack Compose and is not needed here.
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.logic.FocusAppManager
import com.mcfly.shield_ai.model.GuardianInsight // <-- 2. TO-DO: Ensure this class exists and the module is a dependency.
import com.mcfly.shield_ai.voice.GuardianVoiceClient
import java.util.TimeZone

object CoachPopupManager {

    private var activePopup: PopupWindow? = null

    // 2. TO-DO: Make sure the 'GuardianInsight' class is correctly defined and imported.
    // If it's in another module (e.g., a 'model' or 'data' module),
    // ensure 'app' has a dependency on it in your build.gradle file.
    // implementation(project(":model"))
    fun showCoachingPopup(context: Context, insight: GuardianInsight) {
        activePopup?.dismiss()

        if (context !is Activity || context.isFinishing) {
            Log.e("CoachPopupManager", "Cannot show popup. Invalid context or activity is finishing.")
            return
        }

        val inflater = LayoutInflater.from(context)
        // 3. TO-DO: Make sure a layout file named 'popup_guardian_coach.xml' exists in 'app/src/main/res/layout/'.
        val popupView = inflater.inflate(R.layout.popup_guardian_coach, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            isFocusable = true
            // 4. TO-DO: Ensure a style named 'GuardianPopupAnimation' exists in a style file (e.g., 'themes.xml').
            animationStyle = R.style.GuardianPopupAnimation
        }

        // 5. TO-DO: Check your 'popup_guardian_coach.xml' file and confirm all these IDs are correct.
        val suggestionText = popupView.findViewById<TextView>(R.id.guardianSuggestionText)
        val closeButton = popupView.findViewById<Button>(R.id.closeButton)
        val refocusButton = popupView.findViewById<Button>(R.id.actionRefocus)
        val remindLaterButton = popupView.findViewById<Button>(R.id.actionLater)
        val feedbackPositive = popupView.findViewById<TextView>(R.id.feedbackPositive)
        val feedbackNegative = popupView.findViewById<TextView>(R.id.feedbackNegative)

        suggestionText.text = "Guardian is thinking..."
        GuardianVoiceClient.generateSuggestion(
            insight = insight,
            onResult = { suggestion -> suggestionText.text = suggestion },
            onError = { errorMessage ->
                suggestionText.text = "Guardian is currently offline. Please try again later."
                Log.e("CoachPopupManager", errorMessage)
            }
        )

        refocusButton.setOnClickListener {
            FocusAppManager.launchOrChooseFocusApp(context)
            popupWindow.dismiss()
        }

        remindLaterButton.setOnClickListener {
            val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Recenter: Take a mindful pause")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 3600000)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 5400000)
                putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            if (calendarIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(calendarIntent)
            } else {
                Toast.makeText(context, "No calendar app found to set reminder.", Toast.LENGTH_SHORT).show()
            }
            popupWindow.dismiss()
        }

        closeButton.setOnClickListener { popupWindow.dismiss() }

        fun handleFeedback(sentiment: String) {
            Toast.makeText(context, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
            try {
                com.mcfly.shield_ai.sync.GuardianLogger.getInstance()
                    .trackInsightFeedback(insight.id ?: "unknown", sentiment)
            } catch (e: Exception) {
                Log.e("CoachPopupManager", "Failed to record feedback", e)
            }
            popupWindow.dismiss()
        }

        feedbackPositive.setOnClickListener { handleFeedback("positive") }
        feedbackNegative.setOnClickListener { handleFeedback("negative") }

        val rootView = context.window.decorView.rootView
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 100)

        val autoDismissHandler = Handler(Looper.getMainLooper())
        val autoDismissRunnable = Runnable {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        autoDismissHandler.postDelayed(autoDismissRunnable, 10000)

        popupWindow.setOnDismissListener {
            autoDismissHandler.removeCallbacks(autoDismissRunnable)
            activePopup = null
        }

        activePopup = popupWindow
    }

    fun dismissPopup() {
        activePopup?.dismiss()
        activePopup = null
    }
}
