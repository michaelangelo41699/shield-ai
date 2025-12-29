package com.mcfly.shield_ai.data.local.entities

import com.mcfly.shield_ai.network.GuardianInsight
import java.time.Instant

fun GuardianInsightEntity.toDomain(): GuardianInsight {
    return GuardianInsight(
        label = label,
        confidence = confidence,
        sourceText = sourceText,
        patternName = patternName,
        category = category,
        metadata = metadata,
        timestamp = Instant.ofEpochMilli(timestamp), // Convert Long → Instant
        influenceType = influenceType,
        interruptionLevel = interruptionLevel,
        deliveryMechanism = deliveryMechanism,
        influenceCategory = influenceCategory,
        influenceSubCategory = influenceSubCategory,
        monetizationType = monetizationType,
        logContext = logContext,
        userSegment = userSegment,
        outcome = outcome,
        trigger = trigger
    )
}

fun GuardianInsight.toEntity(): GuardianInsightEntity {
    return GuardianInsightEntity(
        label = label,
        confidence = confidence,
        sourceText = sourceText,
        patternName = patternName,
        category = category,
        metadata = metadata,
        timestamp = timestamp.toEpochMilli(), // Convert Instant → Long
        influenceType = influenceType,
        interruptionLevel = interruptionLevel,
        deliveryMechanism = deliveryMechanism,
        influenceCategory = influenceCategory,
        influenceSubCategory = influenceSubCategory,
        monetizationType = monetizationType,
        logContext = logContext,
        userSegment = userSegment,
        outcome = outcome,
        trigger = trigger
    )
}
