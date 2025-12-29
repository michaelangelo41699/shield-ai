package com.mcfly.shield_ai.model.influence

/**
 * More specific type within the influence category.
 */
enum class InfluenceSubCategory {
    /**
     * Ads in various formats (banners, videos, sponsored).
     */
    ADVERTISEMENT,

    /**
     * System or app notifications.
     */
    NOTIFICATION,

    /**
     * Social interactions like mentions, likes, replies.
     */
    INTERACTION,

    /**
     * Feed items, videos, posts, articles, etc.
     */
    CONTENT,

    /**
     * Messages, chats, emails.
     */
    MESSAGING,

    /**
     * Warnings, security notices, critical alerts.
     */
    ALERT,

    /**
     * Price alerts, restock notices, purchase nudges.
     */
    E_COMMERCE,

    /**
     * Email campaigns, newsletters, promotions.
     */
    EMAIL,

    /**
     * Not specified or uncategorized.
     */
    UNSPECIFIED
}
