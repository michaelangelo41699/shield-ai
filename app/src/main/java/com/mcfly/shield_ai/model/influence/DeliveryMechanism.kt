package com.mcfly.shield_ai.model.influence

/**
 * Defines how the influence was delivered to the user.
 */
enum class DeliveryMechanism {
    /**
     * Information actively sent to the user (e.g., notifications, emails).
     */
    PUSH,

    /**
     * User actively retrieves the information (e.g., refreshing a feed, searching).
     */
    PULL,

    /**
     * Internal system-generated events, not directly user-facing.
     */
    SYSTEM,

    /**
     * Mechanism not specified or unknown.
     */
    UNSPECIFIED
}
