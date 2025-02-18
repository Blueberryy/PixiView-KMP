package me.matsumo.fanbox.feature.creator.download

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.common.util.suspendRunCatching
import me.matsumo.fanbox.core.model.Destination
import me.matsumo.fanbox.core.model.ScreenState
import me.matsumo.fanbox.core.model.updateWhenIdle
import me.matsumo.fanbox.core.repository.DownloadPostsRepository
import me.matsumo.fanbox.core.repository.FanboxRepository
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.error_network
import me.matsumo.fanbox.core.ui.customNavTypes
import me.matsumo.fankt.fanbox.domain.FanboxCursor
import me.matsumo.fankt.fanbox.domain.model.FanboxCreatorDetail
import me.matsumo.fankt.fanbox.domain.model.FanboxPost

class CreatorPostsDownloadViewModel(
    savedStateHandle: SavedStateHandle,
    private val fanboxRepository: FanboxRepository,
    private val downloadPostsRepository: DownloadPostsRepository,
) : ViewModel() {

    private val creatorId = savedStateHandle.toRoute<Destination.CreatorPostsDownload>(customNavTypes).creatorId

    private val _screenState = MutableStateFlow<ScreenState<CreatorPostsDownloadUiState>>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                val creatorDetail = fanboxRepository.getCreatorDetail(creatorId)
                val paginate = fanboxRepository.getCreatorPostsPagination(creatorId)

                CreatorPostsDownloadUiState(
                    creatorDetail = creatorDetail,
                    postsPaginate = paginate,
                    posts = mutableListOf(),
                    targetPosts = mutableListOf(),
                    ignoreKeyword = "",
                    isIgnoreFreePosts = false,
                    isIgnoreFiles = false,
                    isPrepared = false,
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(Res.string.error_network) },
            )
        }
    }

    suspend fun fetchPosts(
        paginate: List<FanboxCursor>,
        updateCallback: (Float) -> Unit,
    ) {
        val max = paginate.sumOf { it.limit ?: 10 }
        val posts = mutableListOf<FanboxPost>()

        for (cursor in paginate) {
            posts.addAll(fanboxRepository.getCreatorPosts(creatorId, cursor, null).contents)
            updateCallback.invoke(posts.size.toFloat() / max)
        }

        updateCallback.invoke(1f)

        val data = posts
            .distinctBy { post -> post.id }
            .filter { !it.isRestricted }
            .map { post ->
                CreatorPostsDownloadData(
                    post = post,
                    isDownloaded = false,
                )
            }

        _screenState.updateWhenIdle {
            it.copy(
                posts = data,
                targetPosts = data,
                isPrepared = true,
            )
        }
    }

    fun download() {
        val data = (screenState.value as? ScreenState.Idle)?.data ?: return

        for (post in data.targetPosts) {
            downloadPostsRepository.requestDownloadPost(post.post, data.isIgnoreFiles)
        }
    }

    fun updateIgnoreKeyword(ignoreKeyword: String) {
        val keywords = ignoreKeyword.split(",").map { it.trim() }.filter { it.isNotBlank() }

        _screenState.updateWhenIdle {
            it.copy(
                targetPosts = it.posts.filter { post ->
                    keywords.none { keyword -> (post.post.title + post.post.excerpt).contains(keyword, ignoreCase = true) }
                },
                ignoreKeyword = ignoreKeyword,
            )
        }
    }

    fun updateIgnoreFreePosts(isIgnoreFreePosts: Boolean) {
        _screenState.updateWhenIdle {
            it.copy(
                targetPosts = it.posts.filter { post -> post.post.feeRequired != 0 },
                isIgnoreFreePosts = isIgnoreFreePosts,
            )
        }
    }

    fun updateIgnoreFiles(isIgnoreFiles: Boolean) {
        _screenState.updateWhenIdle {
            it.copy(
                targetPosts = it.posts.filter { post -> post.post.cover != null },
                isIgnoreFiles = isIgnoreFiles,
            )
        }
    }
}

@Stable
data class CreatorPostsDownloadUiState(
    val creatorDetail: FanboxCreatorDetail,
    val postsPaginate: List<FanboxCursor>,
    val posts: List<CreatorPostsDownloadData>,
    val targetPosts: List<CreatorPostsDownloadData>,
    val ignoreKeyword: String,
    val isIgnoreFreePosts: Boolean,
    val isIgnoreFiles: Boolean,
    val isPrepared: Boolean,
)

@Stable
data class CreatorPostsDownloadData(
    val post: FanboxPost,
    var isDownloaded: Boolean,
)
