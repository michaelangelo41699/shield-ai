package com.mcfly.shield_ai.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response


interface GuardianApi {
    @POST("/analyze-logs")
    suspend fun analyzeLogs(@Body request: AnalyzeRequest): Response<AnalyzeResponse>

}
