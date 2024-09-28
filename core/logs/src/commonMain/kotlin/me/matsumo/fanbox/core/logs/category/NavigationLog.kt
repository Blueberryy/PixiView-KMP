package me.matsumo.fanbox.core.logs.category

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// This class is automatically generated by generate-log-classes.
sealed class NavigationLog : LogCategory {

    class Navigate internal constructor(
        private val screenRoute: String,
        private val referer: String,
    ) : NavigationLog() {
        override val properties: JsonObject = buildJsonObject {
            put("event_category", "navigation")
            put("event_name", "navigate")
            put("screen_route", screenRoute)
            put("referer", referer)
        }
    }

    class OpenUrl internal constructor(
        private val url: String,
        private val referer: String,
        private val isSuccess: Boolean,
    ) : NavigationLog() {
        override val properties: JsonObject = buildJsonObject {
            put("event_category", "navigation")
            put("event_name", "open_url")
            put("url", url)
            put("referer", referer)
            put("is_success", isSuccess)
        }
    }

    companion object {
        // 画面遷移したときのログ
        fun navigate(
            screenRoute: String,
            referer: String,
        ) = Navigate(screenRoute, referer)

        // 外部リンクを開いたときのログ
        fun openUrl(
            url: String,
            referer: String,
            isSuccess: Boolean,
        ) = OpenUrl(url, referer, isSuccess)
    }
}
