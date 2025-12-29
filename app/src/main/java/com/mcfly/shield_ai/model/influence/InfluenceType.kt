package com.mcfly.shield_ai.model.influence

enum class InfluenceType(
    val interruptionLevel: InterruptionLevel,
    val userInitiated: Boolean,
    val deliveryMechanism: DeliveryMechanism,
    val category: InfluenceCategory,
    val subCategory: InfluenceSubCategory,
    val attentionScore: Double,
    val estimatedViewDuration: Int,
    val monetizationType: MonetizationType
) {
    TARGETED_AD(InterruptionLevel.HIGH, false, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.ADVERTISEMENT, 0.9, 3000, MonetizationType.AD_IMPRESSION),
    SPONSORED_CONTENT(InterruptionLevel.MEDIUM, false, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.ADVERTISEMENT, 0.7, 2500, MonetizationType.AD_IMPRESSION),
    NATIVE_AD(InterruptionLevel.LOW, false, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.ADVERTISEMENT, 0.5, 2000, MonetizationType.AD_IMPRESSION),
    PRIORITY_NOTIFICATION(InterruptionLevel.CRITICAL, false, DeliveryMechanism.PUSH, InfluenceCategory.SYSTEM, InfluenceSubCategory.NOTIFICATION, 1.0, 0, MonetizationType.NONE),
    STANDARD_NOTIFICATION(InterruptionLevel.MEDIUM, false, DeliveryMechanism.PUSH, InfluenceCategory.SYSTEM, InfluenceSubCategory.NOTIFICATION, 0.6, 0, MonetizationType.NONE),
    SILENT_NOTIFICATION(InterruptionLevel.LOW, false, DeliveryMechanism.PUSH, InfluenceCategory.SYSTEM, InfluenceSubCategory.NOTIFICATION, 0.3, 0, MonetizationType.NONE),
    SOCIAL_MENTION(InterruptionLevel.HIGH, true, DeliveryMechanism.PUSH, InfluenceCategory.SOCIAL, InfluenceSubCategory.INTERACTION, 0.8, 1500, MonetizationType.NONE),
    SOCIAL_REACTION(InterruptionLevel.MEDIUM, true, DeliveryMechanism.PUSH, InfluenceCategory.SOCIAL, InfluenceSubCategory.INTERACTION, 0.6, 1000, MonetizationType.NONE),
    SOCIAL_SHARE(InterruptionLevel.LOW, true, DeliveryMechanism.PULL, InfluenceCategory.SOCIAL, InfluenceSubCategory.INTERACTION, 0.4, 800, MonetizationType.NONE),
    SOCIAL_FEED_ITEM(InterruptionLevel.LOW, false, DeliveryMechanism.PULL, InfluenceCategory.SOCIAL, InfluenceSubCategory.CONTENT, 0.3, 500, MonetizationType.NONE),
    AUTOPLAY_VIDEO(InterruptionLevel.HIGH, false, DeliveryMechanism.PUSH, InfluenceCategory.MEDIA, InfluenceSubCategory.CONTENT, 0.85, 4000, MonetizationType.AD_IMPRESSION),
    SUGGESTED_VIDEO(InterruptionLevel.MEDIUM, false, DeliveryMechanism.PULL, InfluenceCategory.MEDIA, InfluenceSubCategory.CONTENT, 0.6, 3000, MonetizationType.NONE),
    BACKGROUND_AUDIO(InterruptionLevel.LOW, false, DeliveryMechanism.PUSH, InfluenceCategory.MEDIA, InfluenceSubCategory.CONTENT, 0.4, 2000, MonetizationType.NONE),
    DIRECT_MESSAGE(InterruptionLevel.CRITICAL, true, DeliveryMechanism.PUSH, InfluenceCategory.COMMUNICATION, InfluenceSubCategory.MESSAGING, 1.0, 0, MonetizationType.NONE),
    GROUP_MESSAGE(InterruptionLevel.HIGH, true, DeliveryMechanism.PUSH, InfluenceCategory.COMMUNICATION, InfluenceSubCategory.MESSAGING, 0.8, 0, MonetizationType.NONE),
    CHAT_NOTIFICATION(InterruptionLevel.MEDIUM, true, DeliveryMechanism.PUSH, InfluenceCategory.COMMUNICATION, InfluenceSubCategory.MESSAGING, 0.6, 0, MonetizationType.NONE),
    SYSTEM_ALERT(InterruptionLevel.CRITICAL, false, DeliveryMechanism.SYSTEM, InfluenceCategory.SYSTEM, InfluenceSubCategory.ALERT, 1.0, 0, MonetizationType.NONE),
    SECURITY_WARNING(InterruptionLevel.CRITICAL, false, DeliveryMechanism.SYSTEM, InfluenceCategory.SECURITY, InfluenceSubCategory.ALERT, 1.0, 0, MonetizationType.NONE),
    MAINTENANCE_NOTICE(InterruptionLevel.HIGH, false, DeliveryMechanism.SYSTEM, InfluenceCategory.SYSTEM, InfluenceSubCategory.ALERT, 0.7, 0, MonetizationType.NONE),
    PRICE_ALERT(InterruptionLevel.MEDIUM, true, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.E_COMMERCE, 0.6, 1800, MonetizationType.CLICK_THROUGH),
    STOCK_NOTIFICATION(InterruptionLevel.MEDIUM, true, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.E_COMMERCE, 0.5, 1600, MonetizationType.CLICK_THROUGH),
    RECOMMENDATION(InterruptionLevel.LOW, false, DeliveryMechanism.PULL, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.E_COMMERCE, 0.3, 1200, MonetizationType.NONE),
    PRIORITY_EMAIL(InterruptionLevel.HIGH, false, DeliveryMechanism.PUSH, InfluenceCategory.COMMUNICATION, InfluenceSubCategory.EMAIL, 0.7, 0, MonetizationType.NONE),
    NEWSLETTER(InterruptionLevel.LOW, false, DeliveryMechanism.PULL, InfluenceCategory.COMMUNICATION, InfluenceSubCategory.EMAIL, 0.2, 900, MonetizationType.NONE),
    PROMOTIONAL_EMAIL(InterruptionLevel.LOW, false, DeliveryMechanism.PUSH, InfluenceCategory.COMMERCIAL, InfluenceSubCategory.EMAIL, 0.4, 1100, MonetizationType.AD_IMPRESSION),
    UNKNOWN(InterruptionLevel.NONE, false, DeliveryMechanism.UNSPECIFIED, InfluenceCategory.UNKNOWN, InfluenceSubCategory.UNSPECIFIED, 0.0, 0, MonetizationType.NONE);

    fun isMonetized(): Boolean = monetizationType != MonetizationType.NONE

    fun requiresImmediateResponse(): Boolean =
        interruptionLevel == InterruptionLevel.CRITICAL ||
                (interruptionLevel == InterruptionLevel.HIGH && userInitiated)

    fun calculatePriorityScore(): Int {
        var baseScore = interruptionLevel.severity * 1000
        if (userInitiated) baseScore += 500
        if (deliveryMechanism == DeliveryMechanism.PUSH) baseScore += 300
        return baseScore + (attentionScore * 200).toInt()
    }
}

