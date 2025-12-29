package com.mcfly.shield_ai.utils

class GoalProfile {
    var user_id: String? = null
    var life_goals: List<String>? = null
    var ideal_self: String? = null
    var core_values: List<String>? = null
    var emotional_triggers: Map<String, List<String>>? = null
    var daily_focus: String? = null
    var demotivation_signals: List<String>? = null

    class PsychNeed {
        var importance: Int = 0
        var definition: String? = null
        var challenge: String? = null
        var triggers: List<String>? = null
        var barriers: List<String>? = null
        var non_negotiables: List<String>? = null
        var roadblocks: List<String>? = null
        var vision: String? = null
    }

    var psych_needs: Map<String, PsychNeed>? = null
}