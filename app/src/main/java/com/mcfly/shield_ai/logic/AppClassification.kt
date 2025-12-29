package com.mcfly.shield_ai.logic

object AppClassification {

    data class AppInfo(
        val label: String,
        val category: String,
        val riskLevel: String,
        val type: AppType
    )

    enum class AppType {
        RISK,
        WELLNESS
    }

    // 🧠 Unified app map: packageName → AppInfo
    private val apps = mapOf(
        // 🔴 Risk Apps
        "com.twitter.android" to AppInfo("X (formerly Twitter)", "political_outrage", "high", AppType.RISK),
        "com.truthsocial.app" to AppInfo("Truth Social", "political_outrage", "high", AppType.RISK),
        "com.facebook.lite" to AppInfo("Facebook Lite (Political Groups/News Feed)", "political_outrage", "medium", AppType.RISK),
        "com.reddit.frontpage" to AppInfo("Reddit", "doomscrolling", "high", AppType.RISK),
        "com.instagram.android" to AppInfo("Instagram", "comparison_dysphoria", "high", AppType.RISK),
        "com.snapchat.android" to AppInfo("Snapchat", "identity_fragmentation", "high", AppType.RISK),
        "com.tiktok.android" to AppInfo("TikTok", "dopamine_loop", "high", AppType.RISK),
        "com.google.android.youtube" to AppInfo("YouTube", "escapism_binge", "high", AppType.RISK),
        "com.netflix.mediaclient" to AppInfo("Netflix", "escapism_binge", "high", AppType.RISK),
        "com.facebook.katana" to AppInfo("Facebook", "social_overload", "high", AppType.RISK),
        "com.zhiliaoapp.musically" to AppInfo("TikTok (Alt)", "political_outrage", "high", AppType.RISK),
        "com.google.android.apps.magazines" to AppInfo("Google News", "doomscrolling", "medium", AppType.RISK),
        "com.apple.news" to AppInfo("Apple News", "doomscrolling", "medium", AppType.RISK),
        "com.cnn.mobile.android.phone" to AppInfo("CNN", "doomscrolling", "medium", AppType.RISK),
        "com.foxnews.android" to AppInfo("Fox News", "doomscrolling", "medium", AppType.RISK),
        "com.nytimes.android" to AppInfo("The New York Times", "doomscrolling", "medium", AppType.RISK),
        "com.smartnews.android" to AppInfo("SmartNews", "doomscrolling", "medium", AppType.RISK),
        "com.pinterest" to AppInfo("Pinterest", "comparison_dysphoria", "medium", AppType.RISK),
        "com.weheartit" to AppInfo("WeHeartIt", "comparison_dysphoria", "high", AppType.RISK),
        "com.lemon.goose" to AppInfo("BeReal", "comparison_dysphoria", "medium", AppType.RISK),
        "com.vsco.cam" to AppInfo("VSCO", "comparison_dysphoria", "low", AppType.RISK),
        "tv.twitch" to AppInfo("Twitch", "dopamine_loop", "high", AppType.RISK),
        "com.smule.singandroid" to AppInfo("Smule", "dopamine_loop", "low", AppType.RISK),
        "com.bytedance.capcut" to AppInfo("CapCut", "dopamine_loop", "low", AppType.RISK),
        "com.hulu.plus" to AppInfo("Hulu", "escapism_binge", "high", AppType.RISK),
        "com.disney.disneyplus" to AppInfo("Disney+", "escapism_binge", "high", AppType.RISK),
        "com.amazon.avod.thirdpartyclient" to AppInfo("Prime Video", "escapism_binge", "medium", AppType.RISK),
        "com.peacock.android" to AppInfo("Peacock", "escapism_binge", "medium", AppType.RISK),
        "com.paramount.plus" to AppInfo("Paramount+", "escapism_binge", "medium", AppType.RISK),
        "com.hbo.max" to AppInfo("Max", "escapism_binge", "high", AppType.RISK),
        "com.crunchyroll.crunchyroid" to AppInfo("Crunchyroll", "escapism_binge", "medium", AppType.RISK),
        "com.spotify.music" to AppInfo("Spotify", "escapism_binge", "low", AppType.RISK),
        "fm.player" to AppInfo("Pandora", "escapism_binge", "low", AppType.RISK),
        "com.google.android.apps.play.movies" to AppInfo("Google TV", "escapism_binge", "medium", AppType.RISK),
        "com.discord" to AppInfo("Discord", "identity_fragmentation", "medium", AppType.RISK),
        "com.facebook.orca" to AppInfo("Messenger", "social_overload", "medium", AppType.RISK),
        "com.whatsapp" to AppInfo("WhatsApp", "social_overload", "medium", AppType.RISK),
        "org.telegram.messenger" to AppInfo("Telegram", "social_overload", "medium", AppType.RISK),
        "com.x.ai.threads" to AppInfo("Threads", "identity_fragmentation", "medium", AppType.RISK),
        "com.roblox.client" to AppInfo("Roblox", "identity_fragmentation", "medium", AppType.RISK),
        "com.microsoft.teams" to AppInfo("Teams", "social_overload", "low", AppType.RISK),
        "com.zoom.videomeetings" to AppInfo("Zoom", "social_overload", "low", AppType.RISK),
        "com.flipboard.app" to AppInfo("Flipboard", "doomscrolling", "medium", AppType.RISK),
        "com.nbcnews.online" to AppInfo("NBC News", "doomscrolling", "medium", AppType.RISK),
        "com.wsj.android" to AppInfo("WSJ", "doomscrolling", "medium", AppType.RISK),
        "com.ap.mobile" to AppInfo("AP News", "doomscrolling", "medium", AppType.RISK),
        "com.reuters.mobile" to AppInfo("Reuters", "doomscrolling", "medium", AppType.RISK),

        // 🟢 Wellness Apps
        "com.calm.android" to AppInfo("Calm", "mindfulness", "low", AppType.WELLNESS),
        "com.headspace.android" to AppInfo("Headspace", "mindfulness", "low", AppType.WELLNESS),
        "com.spotlightsix.managedmind" to AppInfo("Insight Timer", "mindfulness", "low", AppType.WELLNESS),
        "com.tenpercent.tenpercentapp" to AppInfo("Ten Percent Happier", "mindfulness", "low", AppType.WELLNESS),
        "org.wakingup.android" to AppInfo("Waking Up", "mindfulness", "low", AppType.WELLNESS),
        "com.betterme.sleeptracker" to AppInfo("BetterSleep", "mindfulness", "low", AppType.WELLNESS),
        "com.uclamindful" to AppInfo("UCLA Mindful", "mindfulness", "low", AppType.WELLNESS),
        "com.dayoneapp.dayone" to AppInfo("Day One Journal", "journaling", "low", AppType.WELLNESS),
        "com.journey.app" to AppInfo("Journey", "journaling", "low", AppType.WELLNESS),
        "io.reflectly.android" to AppInfo("Reflectly", "journaling", "low", AppType.WELLNESS),
        "com.strides" to AppInfo("Strides", "self_reflection", "low", AppType.WELLNESS),
        "com.lifetimestuff.lifetimeapp" to AppInfo("Lifetime", "self_reflection", "low", AppType.WELLNESS),
        "com.moodfit.moodfit" to AppInfo("Moodfit", "self_reflection", "low", AppType.WELLNESS),
        "com.daylio" to AppInfo("Daylio", "self_reflection", "low", AppType.WELLNESS),
        "com.happify.happify" to AppInfo("Happify", "self_reflection", "low", AppType.WELLNESS),
        "com.forestapp.trees" to AppInfo("Forest", "focus_tools", "low", AppType.WELLNESS),
        "com.notion.android" to AppInfo("Notion", "focus_tools", "low", AppType.WELLNESS),
        "com.todoist" to AppInfo("Todoist", "focus_tools", "low", AppType.WELLNESS),
        "com.ticktick.task" to AppInfo("TickTick", "focus_tools", "low", AppType.WELLNESS),
        "com.anydo" to AppInfo("Any.do", "focus_tools", "low", AppType.WELLNESS),
        "com.google.android.calendar" to AppInfo("Google Calendar", "focus_tools", "low", AppType.WELLNESS),
        "com.microsoft.office.outlook" to AppInfo("Outlook", "focus_tools", "low", AppType.WELLNESS),
        "com.evernote" to AppInfo("Evernote", "focus_tools", "low", AppType.WELLNESS),
        "com.google.android.keep" to AppInfo("Google Keep", "focus_tools", "low", AppType.WELLNESS),
        "com.asana.app" to AppInfo("Asana", "focus_tools", "low", AppType.WELLNESS),
        "com.slack" to AppInfo("Slack", "focus_tools", "low", AppType.WELLNESS),
        "com.duolingo" to AppInfo("Duolingo", "skill_building", "low", AppType.WELLNESS),
        "org.khanacademy.android" to AppInfo("Khan Academy", "skill_building", "low", AppType.WELLNESS),
        "org.coursera.android" to AppInfo("Coursera", "skill_building", "low", AppType.WELLNESS),
        "org.edx.mobile" to AppInfo("edX", "skill_building", "low", AppType.WELLNESS),
        "com.sololearn" to AppInfo("SoloLearn", "skill_building", "low", AppType.WELLNESS),
        "com.udemy.android" to AppInfo("Udemy", "skill_building", "low", AppType.WELLNESS),
        "com.quizlet.quizletandroid" to AppInfo("Quizlet", "skill_building", "low", AppType.WELLNESS),
        "com.procreate" to AppInfo("Procreate Pocket", "skill_building", "low", AppType.WELLNESS),
        "com.journalitapp" to AppInfo("JournalIt", "journaling", "low", AppType.WELLNESS),
        "com.griddiary.journal" to AppInfo("Grid Diary", "journaling", "low", AppType.WELLNESS),
        "com.fiveminutejournal.android" to AppInfo("Five Minute Journal", "journaling", "low", AppType.WELLNESS),
        "com.penzu.app" to AppInfo("Penzu", "journaling", "low", AppType.WELLNESS),
        "com.myfitnesspal.android" to AppInfo("MyFitnessPal", "physical_wellness", "low", AppType.WELLNESS),
        "com.fiton.app" to AppInfo("FitOn", "physical_wellness", "low", AppType.WELLNESS),
        "com.strava" to AppInfo("Strava", "physical_wellness", "low", AppType.WELLNESS),
        "com.nike.ntc" to AppInfo("Nike Training Club", "physical_wellness", "low", AppType.WELLNESS),
        "com.onepeloton.app" to AppInfo("Peloton", "physical_wellness", "low", AppType.WELLNESS),
        "com.google.android.apps.fitness" to AppInfo("Google Fit", "physical_wellness", "low", AppType.WELLNESS),
        "com.apple.Fitness" to AppInfo("Apple Fitness+", "physical_wellness", "low", AppType.WELLNESS),
        "com.fitbit.FitbitMobile" to AppInfo("Fitbit", "physical_wellness", "low", AppType.WELLNESS),
        "com.alltrails.alltrails" to AppInfo("AllTrails", "physical_wellness", "low", AppType.WELLNESS),
        "com.bodyfast" to AppInfo("BodyFast", "physical_wellness", "low", AppType.WELLNESS),
        "com.amazon.kindle" to AppInfo("Kindle", "learning_mindset", "low", AppType.WELLNESS),
        "com.ideashower.readitlater.pro" to AppInfo("Pocket", "learning_mindset", "low", AppType.WELLNESS),
        "com.medium.reader" to AppInfo("Medium", "learning_mindset", "low", AppType.WELLNESS),
        "com.audible.application" to AppInfo("Audible", "learning_mindset", "low", AppType.WELLNESS),
        "com.goodreads" to AppInfo("Goodreads", "learning_mindset", "low", AppType.WELLNESS),
        "com.blinkist.android" to AppInfo("Blinkist", "learning_mindset", "low", AppType.WELLNESS),
        "com.getabstract" to AppInfo("getAbstract", "learning_mindset", "low", AppType.WELLNESS)
    )

    fun getAppInfo(packageName: String): AppInfo? = apps[packageName]

    fun isRiskApp(packageName: String): Boolean =
        apps[packageName]?.type == AppType.RISK

    fun isWellnessApp(packageName: String): Boolean =
        apps[packageName]?.type == AppType.WELLNESS

    fun getRiskLevel(packageName: String): String? =
        apps[packageName]?.riskLevel
}
