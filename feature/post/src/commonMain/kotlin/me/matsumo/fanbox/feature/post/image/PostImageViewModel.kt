package me.matsumo.fanbox.feature.post.image

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.common.util.suspendRunCatching
import me.matsumo.fanbox.core.model.ScreenState
import me.matsumo.fanbox.core.model.fanbox.FanboxPostDetail
import me.matsumo.fanbox.core.model.fanbox.id.PostId
import me.matsumo.fanbox.core.repository.FanboxRepository
import me.matsumo.fanbox.core.ui.MR
import me.matsumo.fanbox.core.ui.extensition.ImageDownloader

class PostImageViewModel(
    private val fanboxRepository: FanboxRepository,
    private val imageDownloader: ImageDownloader,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<PostImageUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    fun fetch(postId: PostId) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                PostImageUiState(
                    postDetail = fanboxRepository.getPostCached(postId),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(MR.strings.error_network) },
            )
        }
    }

    fun downloadImages(imageItems: List<FanboxPostDetail.ImageItem>, callback: () -> Unit) {
        imageDownloader.downloadImages(imageItems, callback)
    }
}

@Stable
data class PostImageUiState(
    val postDetail: FanboxPostDetail,
)
