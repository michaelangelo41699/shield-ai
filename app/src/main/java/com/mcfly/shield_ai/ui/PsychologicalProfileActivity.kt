package com.mcfly.shield_ai.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mcfly.shield_ai.R

class PsychologicalProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychological_profile)

        val prefs = getSharedPreferences("ShieldAI", MODE_PRIVATE)

        // Get saved values from onboarding
        val motivation = prefs.getString("motivation", "Not set")
        val flaws = prefs.getString("perceived_flaws", "Not set")
        val trauma = prefs.getString("trauma", "Not set")
        val coping = prefs.getString("coping", "Not set")
        val happiness = prefs.getString("ideal_happiness", "Not set")
        val peace = prefs.getString("ideal_peace", "Not set")
        val confidence = prefs.getString("ideal_confidence", "Not set")

        // Bind views and set text
        findViewById<TextView>(R.id.motivationView).text = motivation
        findViewById<TextView>(R.id.flawsView).text = flaws
        findViewById<TextView>(R.id.traumaView).text = trauma
        findViewById<TextView>(R.id.copingView).text = coping
        findViewById<TextView>(R.id.happinessView).text = happiness
        findViewById<TextView>(R.id.peaceView).text = peace
        findViewById<TextView>(R.id.confidenceView).text = confidence
    }
}
