package com.mcfly.shield_ai.model.influence

/**
 * High-level classification of the influence source.
 */
enum class InfluenceCategory {
    /**
     * Advertising, sales, e-commerce content.
     */
    COMMERCIAL,

    /**
     * System operations, alerts, notifications.
     */
    SYSTEM,

    /**
     * Social media content and interactions.
     */
    SOCIAL,

    /**
     * Audio/video content, streaming.
     */
    MEDIA,

    /**
     * Messaging, email, communication platforms.
     */
    COMMUNICATION,

    /**
     * Security-related alerts or warnings.
     */
    SECURITY,

    /**
     * Unknown or uncategorized source.
     */
    UNKNOWN
}
