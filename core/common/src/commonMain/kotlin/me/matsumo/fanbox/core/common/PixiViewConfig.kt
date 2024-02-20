package me.matsumo.fanbox.core.common

data class PixiViewConfig(
    val versionCode: String,
    val versionName: String,
    val developerPassword: String,
    val pixivClientId: String,
    val pixivClientSecret: String,
    val adMobAndroid: AdMob,
    val adMobIos: AdMob,
) {
    data class AdMob(
        val appId: String,
        val bannerAdUnitId: String,
        val nativeAdUnitId: String,
    ) {
        companion object {
            fun dummy(): AdMob {
                return AdMob(
                    appId = "ca-app-pub-1919191919~1919191919",
                    bannerAdUnitId = "ca-app-pub-1919191919~1919191919",
                    nativeAdUnitId = "ca-app-pub-1919191919~1919191919",
                )
            }
        }
    }

    companion object {
        fun dummy(): PixiViewConfig {
            return PixiViewConfig(
                versionCode = "223",
                versionName = "1.4.21",
                developerPassword = "1919191919",
                pixivClientId = "1919191919",
                pixivClientSecret = "1919191919",
                adMobAndroid = AdMob.dummy(),
                adMobIos = AdMob.dummy(),
            )
        }
    }
}
