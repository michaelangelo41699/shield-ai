package com.mcfly.shield_ai.data.local.entities

import androidx.room.*
import com.mcfly.shield_ai.model.influence.*
import com.mcfly.shield_ai.network.GuardianInsight
import java.time.Instant

@Entity(tableName = "guardian_insights")
data class GuardianInsightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val label: String,
    val confidence: Float,
    val sourceText: String,
    val patternName: String?,
    val category: String,

    val timestamp: Long, // Store as epoch millis

    @ColumnInfo(name = "influence_type")
    val influenceType: InfluenceType,

    @ColumnInfo(name = "interruption_level")
    val interruptionLevel: InterruptionLevel,

    @ColumnInfo(name = "delivery_mechanism")
    val deliveryMechanism: DeliveryMechanism,

    @ColumnInfo(name = "influence_category")
    val influenceCategory: InfluenceCategory,

    @ColumnInfo(name = "influence_subcategory")
    val influenceSubCategory: InfluenceSubCategory,

    @ColumnInfo(name = "monetization_type")
    val monetizationType: MonetizationType,

    @ColumnInfo(name = "log_context")
    val logContext: LogContext,

    @ColumnInfo(name = "user_segment")
    val userSegment: UserSegment,

    @ColumnInfo(name = "action_outcome")
    val outcome: ActionOutcome,

    @ColumnInfo(name = "event_trigger")
    val trigger: EventTrigger,

    @ColumnInfo(name = "metadata")
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        fun from(insight: GuardianInsight): GuardianInsightEntity {
            return GuardianInsightEntity(
                label = insight.label,
                confidence = insight.confidence,
                sourceText = insight.sourceText,
                patternName = insight.patternName,
                category = insight.category,
                timestamp = insight.timestamp.toEpochMilli(),
                influenceType = insight.influenceType,
                interruptionLevel = insight.interruptionLevel,
                deliveryMechanism = insight.deliveryMechanism,
                influenceCategory = insight.influenceCategory,
                influenceSubCategory = insight.influenceSubCategory,
                monetizationType = insight.monetizationType,
                logContext = insight.logContext,
                userSegment = insight.userSegment,
                outcome = insight.outcome,
                trigger = insight.trigger,
                metadata = insight.metadata
            )
        }
    }

    fun toModel(): GuardianInsight {
        return GuardianInsight(
            label = label,
            confidence = confidence,
            sourceText = sourceText,
            patternName = patternName,
            category = category,
            timestamp = Instant.ofEpochMilli(timestamp),
            influenceType = influenceType,
            interruptionLevel = interruptionLevel,
            deliveryMechanism = deliveryMechanism,
            influenceCategory = influenceCategory,
            influenceSubCategory = influenceSubCategory,
            monetizationType = monetizationType,
            logContext = logContext,
            userSegment = userSegment,
            outcome = outcome,
            trigger = trigger,
            metadata = metadata
        )
    }
}
