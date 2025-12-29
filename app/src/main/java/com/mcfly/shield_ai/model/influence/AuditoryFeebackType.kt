package com.mcfly.shield_ai.model.influence

/**
 * Represents the type of audio feedback triggered by an influence event.
 */
enum class AuditoryFeedbackType {
    /**
     * No sound is played.
     */
    NONE,

    /**
     * A sound event occurs but it's inaudible (e.g., silent vibration tone).
     */
    SILENT,

    /**
     * Plays the system's default notification sound.
     */
    DEFAULT_SOUND,

    /**
     * Plays a critical alert sound, typically overriding silent modes.
     */
    CRITICAL_ALERT_SOUND,

    /**
     * A custom, app-defined sound file is played.
     */
    CUSTOM_SOUND
}
