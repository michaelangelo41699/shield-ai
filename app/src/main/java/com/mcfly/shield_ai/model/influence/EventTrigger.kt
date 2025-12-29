package com.mcfly.shield_ai.model.influence

/**
 * Describes what initiated the loggable event.
 */
enum class EventTrigger {
    /**
     * A user-initiated action (e.g., button click, swipe).
     */
    USER_ACTION,

    /**
     * A task triggered by a scheduler (e.g., cron, job manager).
     */
    SCHEDULED_TASK,

    /**
     * A system-generated trigger (e.g., OS event, lifecycle callback).
     */
    SYSTEM_EVENT,

    /**
     * A trigger resulting from an API request or webhook.
     */
    API_CALL,

    /**
     * Initiated by an external service, SDK, or integration.
     */
    EXTERNAL_INTEGRATION,

    /**
     * Triggered due to threshold rules (e.g., usage exceeded, alert raised).
     */
    THRESHOLD_BREACH
}
