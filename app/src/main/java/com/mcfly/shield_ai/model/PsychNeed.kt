package com.mcfly.shield_ai.model

data class PsychNeed(
    val importance: Int? = null,
    val definition: String? = null,
    val challenge: String? = null,
    val triggers: List<String> = emptyList(),
    val barriers: List<String> = emptyList(),
    val nonNegotiables: List<String> = emptyList(),
    val roadblocks: List<String> = emptyList(),
    val vision: String? = null,
    val needsGuardianHelp: Boolean = false
)
