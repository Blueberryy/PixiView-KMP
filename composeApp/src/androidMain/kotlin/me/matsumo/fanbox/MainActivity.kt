package me.matsumo.fanbox

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.logs.category.ApplicationLog
import me.matsumo.fanbox.core.logs.logger.LogConfigurator
import me.matsumo.fanbox.core.logs.logger.send
import me.matsumo.fanbox.core.model.ThemeConfig
import me.matsumo.fanbox.core.repository.UserDataRepository
import me.matsumo.fanbox.core.ui.theme.shouldUseDarkTheme
import org.koin.compose.KoinContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : FragmentActivity(), KoinComponent {

    private val userDataRepository: UserDataRepository by inject()

    private var stayTime = 0L

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            KoinContext {
                val userData by userDataRepository.userData.collectAsStateWithLifecycle(null)
                val isSystemInDarkTheme = shouldUseDarkTheme(userData?.themeConfig ?: ThemeConfig.System)
                val windowSize = calculateWindowSizeClass()

                val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
                val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

                DisposableEffect(isSystemInDarkTheme) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isSystemInDarkTheme },
                        navigationBarStyle = SystemBarStyle.auto(lightScrim, darkScrim) { isSystemInDarkTheme },
                    )
                    onDispose {}
                }

                PixiViewApp(
                    modifier = Modifier.fillMaxSize(),
                    windowSize = windowSize.widthSizeClass,
                    nativeViews = emptyMap(),
                )

                splashScreen.setKeepOnScreenCondition { userData == null }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (stayTime == 0L) {
            lifecycleScope.launch {
                // LogConfigurator が初期化されるまで待つ
                LogConfigurator.isConfigured.first { it }

                stayTime = System.currentTimeMillis()
                ApplicationLog.open().send()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (stayTime != 0L) {
            ApplicationLog.close(System.currentTimeMillis() - stayTime).send()
            stayTime = 0L
        }
    }
}
