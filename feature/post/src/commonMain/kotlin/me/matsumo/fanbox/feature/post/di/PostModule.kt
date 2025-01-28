package me.matsumo.fanbox.feature.post.di

import me.matsumo.fanbox.feature.post.bookmark.BookmarkedPostsViewModel
import me.matsumo.fanbox.feature.post.detail.PostDetailRootViewModel
import me.matsumo.fanbox.feature.post.detail.PostDetailViewModel
import me.matsumo.fanbox.feature.post.image.PostImageViewModel
import me.matsumo.fanbox.feature.post.queue.DownloadQueueViewModel
import me.matsumo.fanbox.feature.post.search.common.PostSearchViewModel
import me.matsumo.fanbox.feature.post.search.creator.PostByCreatorSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val postModule = module {
    viewModelOf(::BookmarkedPostsViewModel)
    viewModelOf(::PostImageViewModel)
    viewModelOf(::PostSearchViewModel)
    viewModelOf(::DownloadQueueViewModel)

    viewModel {
        PostDetailRootViewModel(
            postId = it[0],
            type = it[1],
            userDataRepository = get(),
            fanboxRepository = get(),
        )
    }

    viewModel {
        PostDetailViewModel(
            postId = it[0],
            userDataRepository = get(),
            fanboxRepository = get(),
            downloadPostsRepository = get(),
        )
    }

    viewModel {
        PostByCreatorSearchViewModel(
            creatorId = it.get(),
            userDataRepository = get(),
            fanboxRepository = get(),
        )
    }
}
