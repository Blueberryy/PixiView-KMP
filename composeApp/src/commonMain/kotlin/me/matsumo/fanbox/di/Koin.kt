package me.matsumo.fanbox.di

import me.matsumo.fanbox.core.datastore.di.dataStoreHelperModule
import me.matsumo.fanbox.core.datastore.di.dataStoreModule
import me.matsumo.fanbox.core.repository.di.repositoryModule
import me.matsumo.fanbox.feature.about.di.aboutModule
import me.matsumo.fanbox.feature.creator.di.creatorModule
import me.matsumo.fanbox.feature.library.di.libraryModule
import me.matsumo.fanbox.feature.post.di.postModule
import me.matsumo.fanbox.feature.setting.di.settingModule
import me.matsumo.fanbox.feature.welcome.di.welcomeModule
import org.koin.core.KoinApplication

fun KoinApplication.applyModules() {
    modules(appModule)

    modules(dataStoreModule)
    modules(dataStoreHelperModule)
    modules(repositoryModule)

    modules(welcomeModule)
    modules(libraryModule)
    modules(settingModule)
    modules(aboutModule)
    modules(postModule)
    modules(creatorModule)
}
