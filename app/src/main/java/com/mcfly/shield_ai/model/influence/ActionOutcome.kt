package com.mcfly.shield_ai.model.influence

/**
 * Represents the result of a user or system action.
 */
enum class ActionOutcome {
    /**
     * Action was successfully completed.
     */
    SUCCESS,

    /**
     * Action failed to complete as intended.
     */
    FAILURE,

    /**
     * Action was disregarded or not acted upon.
     */
    IGNORED,

    /**
     * Action was postponed.
     */
    DELAYED,

    /**
     * Action was dismissed by the user or system.
     */
    DISMISSED,

    /**
     * Action was completed with expected outcome.
     */
    COMPLETED,

    /**
     * Action was partially completed or only some steps were successful.
     */
    PARTIALLY_COMPLETED,

    /**
     * An error occurred during execution.
     */
    ERROR
}
