package com.mcfly.shield_ai.guardian

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.ui.ReflectionActivity
import com.mcfly.shield_ai.util.FocusAppLauncher
import com.mcfly.shield_ai.util.ReminderUtils

class GuardianInterventionService : Service() {

    private lateinit var windowManager: WindowManager
    private var popupView: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val insightText = intent?.getStringExtra("INSIGHT_TEXT") ?: ""
        val suggestion = intent?.getStringExtra("SUGGESTION_TEXT") ?: ""

        if (Settings.canDrawOverlays(this)) {
            showPopup(insightText, suggestion)
        } else {
            Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
        }

        return START_NOT_STICKY
    }

    private fun showPopup(insightText: String, suggestionText: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.dialog_guardian_popup, null)

        val insightMessage = popupView!!.findViewById<TextView>(R.id.guardianInsightText)
        val suggestionAction = popupView!!.findViewById<TextView>(R.id.guardianSuggestionText)
        val feedbackPositive = popupView!!.findViewById<ImageButton>(R.id.feedbackThumbsUp)
        val feedbackNeutral = popupView!!.findViewById<ImageButton>(R.id.feedbackNeutral)
        val feedbackNegative = popupView!!.findViewById<ImageButton>(R.id.feedbackThumbsDown)

        insightMessage.text = insightText
        suggestionAction.text = suggestionText

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.CENTER

        popupView!!.findViewById<View>(R.id.actionRefocus).setOnClickListener {
            FocusAppLauncher.launchForest(this)
            removePopup()
        }

        popupView!!.findViewById<View>(R.id.actionJournal).setOnClickListener {
            val i = Intent(this, ReflectionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(i)
            removePopup()
        }

        popupView!!.findViewById<View>(R.id.actionReminder).setOnClickListener {
            ReminderUtils.scheduleReminder(this, 60)
            removePopup()
        }

        feedbackPositive.setOnClickListener {
            // TODO: log to API
            removePopup()
        }

        feedbackNeutral.setOnClickListener {
            // TODO: log to API
            removePopup()
        }

        feedbackNegative.setOnClickListener {
            // TODO: log to API
            removePopup()
        }

        windowManager.addView(popupView, layoutParams)
    }

    private fun removePopup() {
        popupView?.let {
            windowManager.removeView(it)
            popupView = null
        }
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removePopup()
    }

    companion object {
        fun requestOverlayPermission(context: Context) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + context.packageName)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

        fun launchIntervention(context: Context, insight: String, suggestion: String) {
            if (Settings.canDrawOverlays(context)) {
                val intent = Intent(context, GuardianInterventionService::class.java).apply {
                    putExtra("INSIGHT_TEXT", insight)
                    putExtra("SUGGESTION_TEXT", suggestion)
                }
                context.startService(intent)
            } else {
                requestOverlayPermission(context)
            }
        }
    }
}
