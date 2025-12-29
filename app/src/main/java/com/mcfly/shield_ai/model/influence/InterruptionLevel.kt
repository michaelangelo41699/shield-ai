package com.mcfly.shield_ai.model.influence

import com.mcfly.shield_ai.model.influence.AuditoryFeedbackType
import com.mcfly.shield_ai.model.influence.HapticFeedbackType

enum class InterruptionLevel(
    val severity: Int,
    val colorCode: String,
    val bypassDnD: Boolean,
    val requiresAcknowledgement: Boolean,
    val auditoryFeedback: AuditoryFeedbackType,
    val hapticFeedback: HapticFeedbackType
) {
    CRITICAL(
        4, "#FF0000", true, true,
        AuditoryFeedbackType.CRITICAL_ALERT_SOUND,
        HapticFeedbackType.STRONG_VIBRATION
    ),
    HIGH(
        3, "#FF4500", true, false,
        AuditoryFeedbackType.DEFAULT_SOUND,
        HapticFeedbackType.MEDIUM_VIBRATION
    ),
    MEDIUM(
        2, "#FFA500", false, false,
        AuditoryFeedbackType.DEFAULT_SOUND,
        HapticFeedbackType.LIGHT_VIBRATION
    ),
    LOW(
        1, "#FFFF00", false, false,
        AuditoryFeedbackType.NONE,
        HapticFeedbackType.NONE
    ),
    NONE(
        0, "#808080", false, false,
        AuditoryFeedbackType.SILENT,
        HapticFeedbackType.NONE
    ),
    UNSPECIFIED(
        -1, "#000000", false, false,
        AuditoryFeedbackType.NONE,
        HapticFeedbackType.NONE
    );

    fun shouldTriggerHapticFeedback(): Boolean = hapticFeedback != HapticFeedbackType.NONE

    fun shouldPlaySound(): Boolean =
        auditoryFeedback != AuditoryFeedbackType.NONE && auditoryFeedback != AuditoryFeedbackType.SILENT

    companion object {
        fun fromInt(level: Int): InterruptionLevel = when (level) {
            4 -> CRITICAL
            3 -> HIGH
            2 -> MEDIUM
            1 -> LOW
            0 -> NONE
            else -> UNSPECIFIED
        }
    }
}
