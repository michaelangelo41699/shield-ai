package com.mcfly.shield_ai.model.influence

/**
 * Represents how an influence event may be monetized.
 */
enum class MonetizationType {
    /**
     * Revenue is earned when the user sees the ad.
     */
    AD_IMPRESSION,

    /**
     * Revenue is earned when the user clicks on the content.
     */
    CLICK_THROUGH,

    /**
     * Revenue is earned when the user completes a purchase.
     */
    PURCHASE_CONVERSION,

    /**
     * No monetization is directly associated with this event.
     */
    NONE
}
