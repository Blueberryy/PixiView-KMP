package me.matsumo.fanbox.feature.library.discovery

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.common.util.suspendRunCatching
import me.matsumo.fanbox.core.model.ScreenState
import me.matsumo.fanbox.core.repository.FanboxRepository
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.error_no_data_discovery
import me.matsumo.fankt.fanbox.domain.model.FanboxCreatorDetail
import me.matsumo.fankt.fanbox.domain.model.id.FanboxUserId

class LibraryDiscoveryViewModel(
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<LibraryDiscoveryUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            _screenState.value = suspendRunCatching {
                LibraryDiscoveryUiState(
                    followingCreators = fanboxRepository.getFollowingCreators().shuffled(),
                    recommendedCreators = fanboxRepository.getRecommendedCreators(),
                    followingPixivCreators = fanboxRepository.getFollowingPixivCreators(),
                )
            }.fold(
                onSuccess = { ScreenState.Idle(it) },
                onFailure = { ScreenState.Error(Res.string.error_no_data_discovery) },
            )
        }
    }

    suspend fun follow(creatorUserId: FanboxUserId): Result<Unit> {
        return suspendRunCatching {
            fanboxRepository.followCreator(creatorUserId)
        }
    }

    suspend fun unfollow(creatorUserId: FanboxUserId): Result<Unit> {
        return suspendRunCatching {
            fanboxRepository.unfollowCreator(creatorUserId)
        }
    }
}

@Stable
data class LibraryDiscoveryUiState(
    val followingCreators: List<FanboxCreatorDetail>,
    val recommendedCreators: List<FanboxCreatorDetail>,
    val followingPixivCreators: List<FanboxCreatorDetail>,
)
