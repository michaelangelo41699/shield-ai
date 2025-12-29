package com.mcfly.shield_ai.model.influence

/**
 * Describes the technical context in which a log event was generated.
 */
enum class LogContext {
    /**
     * Frontend activity from a web browser interface.
     */
    FRONTEND_WEB,

    /**
     * Frontend activity from a mobile application.
     */
    FRONTEND_MOBILE,

    /**
     * Server-side process or backend system event.
     */
    BACKEND_SERVICE,

    /**
     * Data ingestion or transformation pipeline.
     */
    DATA_PIPELINE,

    /**
     * Integration event from a third-party service or SDK.
     */
    THIRD_PARTY_INTEGRATION,

    /**
     * Admin tools, dashboards, or configuration systems.
     */
    ADMIN_TOOL
}
