package com.mcfly.shield_ai.logic

import com.mcfly.shield_ai.data.local.entities.GoalProfileEntity
import com.mcfly.shield_ai.network.GuardianInsight

data class AlignmentResult(
    val needLabel: String,
    val status: AlignmentStatus,
    val insight: String,
    val reason: String
)

enum class AlignmentStatus {
    ALIGNED, DRIFTING, UNKNOWN
}

object GoalAlignmentAnalyzer {

    fun evaluate(profile: GoalProfileEntity, insights: List<GuardianInsight>): String {
        val results = analyze(profile, insights)

        val hasDrifting = results.any { it.status == AlignmentStatus.DRIFTING }
        val allUnknown = results.all { it.status == AlignmentStatus.UNKNOWN }
        val allAligned = results.all { it.status == AlignmentStatus.ALIGNED }

        return when {
            hasDrifting -> "drifting"
            allAligned -> "aligned"
            allUnknown -> "unknown"
            else -> "unknown"
        }
    }

    fun analyze(
        goalProfile: GoalProfileEntity,
        guardianLogs: List<GuardianInsight>
    ): List<AlignmentResult> {
        val results = mutableListOf<AlignmentResult>()

        for ((label, need) in goalProfile.psychNeeds) {

            if (need.needsGuardianHelp) {
                results.add(
                    AlignmentResult(
                        needLabel = label,
                        status = AlignmentStatus.UNKNOWN,
                        insight = "Guardian is actively learning more about this need.",
                        reason = "This domain has been flagged for observation. No conclusions yet."
                    )
                )
                continue
            }

            val matchedLogs = guardianLogs.filter { log ->
                need.triggers.any { trigger ->
                    log.patternName?.contains(trigger, ignoreCase = true) == true ||
                            log.category?.contains(trigger, ignoreCase = true) == true
                }
            }

            if (matchedLogs.isEmpty()) {
                results.add(
                    AlignmentResult(
                        needLabel = label,
                        status = AlignmentStatus.ALIGNED,
                        insight = "No conflicting patterns detected in recent digital behavior.",
                        reason = "Guardian did not detect any patterns that match known triggers for $label."
                    )
                )
            } else {
                val patternNames = matchedLogs.mapNotNull { it.patternName }.distinct()
                val categories = matchedLogs.mapNotNull { it.category }.distinct()
                val confidence = matchedLogs.maxOfOrNull { it.confidence }?.let { "%.2f".format(it) } ?: "unknown"

                val reasonBuilder = StringBuilder()
                reasonBuilder.append("Detected repeated patterns matching known triggers for $label.\n")
                if (patternNames.isNotEmpty()) {
                    reasonBuilder.append("Triggered patterns: ${patternNames.joinToString(", ")}.\n")
                }
                if (categories.isNotEmpty()) {
                    reasonBuilder.append("Associated behavioral categories: ${categories.joinToString(", ")}.\n")
                }
                reasonBuilder.append("Most confident trigger match: $confidence.")

                results.add(
                    AlignmentResult(
                        needLabel = label,
                        status = AlignmentStatus.DRIFTING,
                        insight = "Behavior may be drifting away from your $label objective.",
                        reason = reasonBuilder.toString()
                    )
                )
            }
        }

        return results
    }
}
