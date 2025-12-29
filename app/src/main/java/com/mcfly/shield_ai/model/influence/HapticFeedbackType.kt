package com.mcfly.shield_ai.model.influence

/**
 * Represents the haptic feedback behavior triggered by an influence event.
 */
enum class HapticFeedbackType {
    /**
     * No vibration or tactile feedback.
     */
    NONE,

    /**
     * A short, soft vibration.
     */
    LIGHT_VIBRATION,

    /**
     * A medium-length, moderate-intensity vibration.
     */
    MEDIUM_VIBRATION,

    /**
     * A strong and possibly attention-grabbing vibration.
     */
    STRONG_VIBRATION,

    /**
     * A custom or patterned vibration sequence.
     */
    PATTERNED_VIBRATION
}
