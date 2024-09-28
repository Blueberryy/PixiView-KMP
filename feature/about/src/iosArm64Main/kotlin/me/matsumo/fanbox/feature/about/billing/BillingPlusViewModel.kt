package me.matsumo.fanbox.feature.about.billing

import androidx.lifecycle.viewModelScope
import coil3.PlatformContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import me.matsumo.fanbox.core.billing.swift.BillingController
import me.matsumo.fanbox.core.common.util.suspendRunCatching
import me.matsumo.fanbox.core.model.ScreenState
import me.matsumo.fanbox.core.repository.UserDataRepository
import me.matsumo.fanbox.core.ui.Res
import me.matsumo.fanbox.core.ui.common_close
import me.matsumo.fanbox.core.ui.error_billing
import kotlin.coroutines.resume

class BillingPlusViewModelImpl(
    private val userDataRepository: UserDataRepository,
) : BillingPlusViewModel() {

    override val screenState = userDataRepository.userData.map { userData ->
        suspendRunCatching {
            callbackFlow {
                BillingController.queryProductWithCompletionHandler { product ->
                    val result = runCatching {
                        BillingPlusUiState(
                            isPlusMode = userData.isPlusMode,
                            isDeveloperMode = userData.isDeveloperMode,
                            formattedPrice = product!!.formattedPrice(),
                        )
                    }

                    trySend(result.getOrThrow())
                }

                awaitClose {
                    // do nothing
                }
            }.first()
        }.fold(
            onSuccess = { ScreenState.Idle(it) },
            onFailure = {
                ScreenState.Error(
                    message = Res.string.error_billing,
                    retryTitle = Res.string.common_close,
                )
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun purchase(context: PlatformContext, planType: BillingPlusUiState.Type): Boolean = suspendCancellableCoroutine { continuation ->
        BillingController.purchaseOnResult(
            onResult = {
                viewModelScope.launch {
                    val isPurchased = (it.toInt() == 0)

                    Napier.d { "isPurchased: $isPurchased" }

                    userDataRepository.setPlusMode(isPurchased)
                    continuation.resume(isPurchased)
                }
            },
            completionHandler = {
                // do nothing
            },
        )
    }

    override suspend fun consume(context: PlatformContext): Boolean {
        return false
    }

    override suspend fun verify(context: PlatformContext): Boolean = suspendCancellableCoroutine { continuation ->
        BillingController.refreshOnResult(
            onResult = {
                viewModelScope.launch {
                    Napier.d { "isPurchased: $it" }

                    userDataRepository.setPlusMode(it)
                    continuation.resume(it)
                }
            },
            completionHandler = {
                // do nothing
            },
        )
    }
}
