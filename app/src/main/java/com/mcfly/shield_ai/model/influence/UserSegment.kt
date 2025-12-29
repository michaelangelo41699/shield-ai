package com.mcfly.shield_ai.model.influence

/**
 * Represents the type or role of the user involved in the event.
 */
enum class UserSegment {
    /**
     * Paying user with premium access or subscription.
     */
    PREMIUM,

    /**
     * User on the free plan or trial version.
     */
    FREE_TIER,

    /**
     * Administrator or internal user.
     */
    ADMIN,

    /**
     * Newly registered user.
     */
    NEW_USER,

    /**
     * User who has been inactive for a period of time.
     */
    INACTIVE_USER,

    /**
     * User participating in beta testing or early access programs.
     */
    BETA_TESTER
}
