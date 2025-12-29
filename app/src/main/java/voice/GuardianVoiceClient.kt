package com.mcfly.shield_ai.voice

import android.util.Log
import com.mcfly.shield_ai.model.GuardianInsight
import com.mcfly.shield_ai.network.VoiceSuggestionRequest
import com.mcfly.shield_ai.network.VoiceSuggestionResponse
import com.mcfly.shield_ai.network.VoiceSuggestionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GuardianVoiceClient {

    fun generateSuggestion(
        insight: GuardianInsight,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = VoiceSuggestionRequest(
            emotion = insight.label,
            trigger = insight.metadata?.trigger,
            driftCause = insight.metadata?.goalMisalignment,
            sourceType = insight.sourceType,
            timestamp = insight.timestamp,
            app = insight.metadata?.appPackage,
            userState = insight.metadata?.userState, // optional future key
            intensity = insight.confidence?.toFloatOrNull() ?: 0.5f
        )

        VoiceSuggestionService.api.getSuggestion(request)
            .enqueue(object : Callback<VoiceSuggestionResponse> {
                override fun onResponse(
                    call: Call<VoiceSuggestionResponse>,
                    response: Response<VoiceSuggestionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(response.body()!!.suggestion)
                    } else {
                        Log.e("GuardianVoice", "Empty response or failure")
                        onError("Could not generate suggestion.")
                    }
                }

                override fun onFailure(call: Call<VoiceSuggestionResponse>, t: Throwable) {
                    Log.e("GuardianVoice", "API call failed: ${t.message}")
                    onError("GuardianVoice unreachable.")
                }
            })
    }
}
