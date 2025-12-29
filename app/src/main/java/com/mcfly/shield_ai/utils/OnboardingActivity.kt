package com.mcfly.shield_ai.utils

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mcfly.shield_ai.R
import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity
import com.mcfly.shield_ai.data.local.repository.GoalProfileRepository
import com.mcfly.shield_ai.model.PsychNeed
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var repository: GoalProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        repository = GoalProfileRepository.getInstance(applicationContext)

        val idealSelfInput = findViewById<EditText>(R.id.idealSelfInput)
        val goal1Input = findViewById<EditText>(R.id.goal1Input)
        val goal2Input = findViewById<EditText>(R.id.goal2Input)
        val goal3Input = findViewById<EditText>(R.id.goal3Input)

        val peaceNeed = collectPsychNeed(
            definitionInput = findViewById(R.id.peaceDefinitionInput),
            challengeInput = findViewById(R.id.peaceChallengeInput),
            triggersInput = findViewById(R.id.peaceTriggersInput),
            barriersInput = findViewById(R.id.peaceBarriersInput),
            nonNegotiablesInput = findViewById(R.id.peaceNonNegotiablesInput),
            roadblocksInput = findViewById(R.id.peaceRoadblocksInput),
            visionInput = findViewById(R.id.peaceVisionInput),
            importanceSeekBar = findViewById(R.id.peaceImportanceSeekBar),
            guardianCheckbox = findViewById(R.id.peaceGuardianCheckbox)
        )

        val confidenceNeed = collectPsychNeed(
            definitionInput = findViewById(R.id.confidenceDefinitionInput),
            challengeInput = findViewById(R.id.confidenceChallengeInput),
            triggersInput = findViewById(R.id.confidenceTriggersInput),
            barriersInput = findViewById(R.id.confidenceBarriersInput),
            nonNegotiablesInput = findViewById(R.id.confidenceNonNegotiablesInput),
            roadblocksInput = findViewById(R.id.confidenceRoadblocksInput),
            visionInput = findViewById(R.id.confidenceVisionInput),
            importanceSeekBar = findViewById(R.id.confidenceImportanceSeekBar),
            guardianCheckbox = findViewById(R.id.confidenceGuardianCheckbox)
        )

        val passionNeed = collectPsychNeed(
            definitionInput = findViewById(R.id.passionDefinitionInput),
            challengeInput = findViewById(R.id.passionChallengeInput),
            triggersInput = findViewById(R.id.passionTriggersInput),
            barriersInput = findViewById(R.id.passionBarriersInput),
            nonNegotiablesInput = findViewById(R.id.passionNonNegotiablesInput),
            roadblocksInput = findViewById(R.id.passionRoadblocksInput),
            visionInput = findViewById(R.id.passionVisionInput),
            importanceSeekBar = findViewById(R.id.passionImportanceSeekBar),
            guardianCheckbox = findViewById(R.id.passionGuardianCheckbox)
        )

        val continueButton = findViewById<Button>(R.id.continueButton)
        continueButton.setOnClickListener {
            val goalProfile = GoalProfileEntity(
                userId = "default",
                idealSelf = idealSelfInput.text.toString(),
                dailyFocus = null,
                demotivationSignals = emptyList(),
                lifeGoals = listOf(
                    goal1Input.text.toString(),
                    goal2Input.text.toString(),
                    goal3Input.text.toString()
                ),
                coreValues = listOf("peace", "confidence", "passion"),
                emotionalTriggers = emptyMap(),
                psychNeeds = mapOf(
                    "peace" to peaceNeed,
                    "confidence" to confidenceNeed,
                    "passion" to passionNeed
                )
            )
            if (idealSelfInput.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter your ideal self before continuing.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                repository.saveProfile(goalProfile)
                startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun collectPsychNeed(
        definitionInput: EditText,
        challengeInput: EditText,
        triggersInput: EditText,
        barriersInput: EditText,
        nonNegotiablesInput: EditText,
        roadblocksInput: EditText,
        visionInput: EditText,
        importanceSeekBar: SeekBar,
        guardianCheckbox: CheckBox
    ): PsychNeed {
        return PsychNeed(
            importance = importanceSeekBar.progress,
            definition = definitionInput.text.toString().takeIf { it.isNotBlank() },
            challenge = challengeInput.text.toString().takeIf { it.isNotBlank() },
            triggers = triggersInput.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
            barriers = barriersInput.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
            nonNegotiables = nonNegotiablesInput.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
            roadblocks = roadblocksInput.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
            vision = visionInput.text.toString().takeIf { it.isNotBlank() },
            needsGuardianHelp = guardianCheckbox.isChecked
        )
    }
}
