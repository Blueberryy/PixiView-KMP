package me.matsumo.fanbox

import android.app.Application
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.decode.GifDecoder
import coil3.decode.ImageDecoderDecoder
import coil3.disk.DiskCache
import coil3.fetch.NetworkFetcher
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.matsumo.fanbox.di.applyModules
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PixiViewApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            androidLogger()
            applyModules()
        }

        if (BuildConfig.DEBUG) {
            // StrictMode.enableDefaults()
            Napier.base(DebugAntilog())
        }

        setupFirebase()
        setupAdMob()
        setupCoil()
    }

    private fun setupAdMob() {
        MobileAds.initialize(this)
        /*MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("33BE2250B43518CCDA7DE426D04EE231"))
                .build()
        )*/
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun setupCoil() {
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(it)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(it, 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(this.cacheDir.resolve("image_cache").toOkioPath())
                        .maxSizePercent(0.02)
                        .build()
                }
                .components {
                    add(NetworkFetcher.Factory())

                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .crossfade(true)
                .build()
        }
    }

    private fun setupFirebase() {
        val builder = FirebaseOptions.Builder()
            .setProjectId("pixiview-b5dc7")
            .setApplicationId(if (!BuildConfig.DEBUG) "1:561281916572:android:925776ddd5d210faab1759" else "1:561281916572:android:85a0c0e2e341d0fdab1759")
            .setApiKey("AIzaSyC6SxUp9ipDpVjQMSHmcgy7SZMnPUwqAmA")
            .setDatabaseUrl("https://pixiview-b5dc7.firebaseio.com")
            .setStorageBucket("pixiview-b5dc7.appspot.com")

        FirebaseApp.initializeApp(this, builder.build())
    }
}
