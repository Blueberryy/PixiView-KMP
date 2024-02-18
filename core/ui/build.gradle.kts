import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("pixiview.primitive.kmp.common")
    id("pixiview.primitive.kmp.android.library")
    id("pixiview.primitive.kmp.android.compose")
    id("pixiview.primitive.kmp.android")
    id("pixiview.primitive.kmp.ios")
    id("pixiview.primitive.kmp.resources")
    id("pixiview.primitive.detekt")
}

android {
    namespace = "me.matsumo.fanbox.core.ui"
}

multiplatformResources {
    multiplatformResourcesPackage = "me.matsumo.fanbox.core.ui"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            resources.srcDir("src/commonMain/resources")

            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:common"))
                implementation(project(":core:repository"))
                implementation(project(":core:datastore"))

                api(libs.bundles.ui.common.api)

                // Compose
                api(compose.runtime)
                api(compose.runtimeSaveable)
                api(compose.foundation)
                api(compose.animation)
                api(compose.animationGraphics)
                api(compose.material)
                api(compose.material3)
                api(compose.ui)
                api(compose.materialIconsExtended)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.components.resources)

                // Resources
                api(libs.moko.resources)
                api(libs.moko.resources.compose)

                api(libs.calf.ui)
                api(libs.androidx.paging.compose)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)

            dependencies {
                api(libs.bundles.ui.android.implementation)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}
