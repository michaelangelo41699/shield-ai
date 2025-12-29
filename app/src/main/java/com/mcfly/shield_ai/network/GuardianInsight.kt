package com.mcfly.shield_ai.network

import com.mcfly.shield_ai.model.influence.*
import java.time.Instant

/**
 * Extended GuardianInsight to support detailed influence tracking.
 */
data class GuardianInsight(
    val label: String,                      // e.g. "doomscrolling", "comparison_dysphoria"
    val confidence: Float,                 // model's confidence score
    val sourceText: String,                // original log text or excerpt
    val patternName: String?,              // matched pattern if applicable
    val category: String,                  // "emotion", "value conflict", etc.
    val metadata: Map<String, String>,     // optional structured data (e.g., severity, sentiment)

    // Enriched fields from logging framework
    val timestamp: Instant,                // time of log event
    val influenceType: InfluenceType,      // ENUM: core behavior category
    val interruptionLevel: InterruptionLevel, // ENUM: CRITICAL, HIGH, etc.
    val deliveryMechanism: DeliveryMechanism,
    val influenceCategory: InfluenceCategory,
    val influenceSubCategory: InfluenceSubCategory,
    val monetizationType: MonetizationType,
    val logContext: LogContext,            // origin of event (frontend, backend, etc.)
    val userSegment: UserSegment,          // FREE_TIER, PREMIUM, etc.
    val outcome: ActionOutcome,            // SUCCESS, IGNORED, etc.
    val trigger: EventTrigger              // What caused it (e.g. USER_ACTION)
)
