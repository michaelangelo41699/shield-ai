package com.mcfly.shield_ai.network

data class AnalyzeRequest(
    val logs: List<LogEntry>  // This is correct as long as LogEntry is in the same package
)
