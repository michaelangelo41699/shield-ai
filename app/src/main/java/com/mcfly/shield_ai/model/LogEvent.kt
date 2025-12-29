package com.mcfly.shield_ai.model

import java.time.Instant
import java.util.UUID
import com.mcfly.shield_ai.model.influence.InfluenceType
import com.mcfly.shield_ai.model.influence.ActionOutcome
import com.mcfly.shield_ai.model.influence.EventTrigger
import com.mcfly.shield_ai.model.influence.LogContext
import com.mcfly.shield_ai.model.influence.UserSegment

// Kotlin data class + builder pattern style for structured behavioral logs
data class LogEvent(
    val eventId: String,
    val timestamp: Instant,
    val influenceType: InfluenceType,
    val userId: String?,
    val sessionId: String?,
    val context: LogContext?,
    val userSegment: UserSegment?,
    val outcome: ActionOutcome?,
    val trigger: EventTrigger?,
    val description: String,
    val additionalMetadata: Map<String, String>
) {
    companion object {
        fun builder(influenceType: InfluenceType, description: String): Builder {
            require(description.isNotBlank()) { "Description must not be blank" }
            return Builder(influenceType, description)
        }
    }

    class Builder(private val influenceType: InfluenceType, private val description: String) {
        private var eventId: String = UUID.randomUUID().toString()
        private var timestamp: Instant = Instant.now()
        private var userId: String? = null
        private var sessionId: String? = null
        private var context: LogContext? = null
        private var userSegment: UserSegment? = null
        private var outcome: ActionOutcome? = null
        private var trigger: EventTrigger? = null
        private val additionalMetadata: MutableMap<String, String> = mutableMapOf()

        fun withUserId(userId: String?) = apply { this.userId = userId }
        fun withSessionId(sessionId: String?) = apply { this.sessionId = sessionId }
        fun withContext(context: LogContext?) = apply { this.context = context }
        fun withUserSegment(userSegment: UserSegment?) = apply { this.userSegment = userSegment }
        fun withOutcome(outcome: ActionOutcome?) = apply { this.outcome = outcome }
        fun withTrigger(trigger: EventTrigger?) = apply { this.trigger = trigger }
        fun addMetadata(key: String, value: String) = apply {
            if (!key.isNullOrBlank() && !value.isNullOrBlank()) {
                this.additionalMetadata[key] = value
            }
        }

        fun build(): LogEvent {
            return LogEvent(
                eventId = eventId,
                timestamp = timestamp,
                influenceType = influenceType,
                userId = userId,
                sessionId = sessionId,
                context = context,
                userSegment = userSegment,
                outcome = outcome,
                trigger = trigger,
                description = description,
                additionalMetadata = additionalMetadata.toMap()
            )
        }
    }

    override fun toString(): String {
        return "LogEvent(eventId='$eventId', timestamp=$timestamp, influenceType=$influenceType, userId=$userId, sessionId=$sessionId, context=$context, userSegment=$userSegment, outcome=$outcome, trigger=$trigger, description='$description', additionalMetadata=$additionalMetadata)"
    }
}
