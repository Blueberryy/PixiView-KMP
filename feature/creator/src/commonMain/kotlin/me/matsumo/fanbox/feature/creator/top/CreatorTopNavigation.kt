package me.matsumo.fanbox.feature.creator.top

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.matsumo.fanbox.core.model.fanbox.id.FanboxCreatorId
import me.matsumo.fanbox.core.model.fanbox.id.FanboxPostId
import me.matsumo.fanbox.core.ui.extensition.navigateWithLog
import me.matsumo.fanbox.core.ui.view.SimpleAlertContents

const val CreatorTopId = "creatorTopId"
const val CreatorTopIsPosts = "creatorTopIsPosts"
const val CreatorTopRoute = "creatorTop/{$CreatorTopId}/{$CreatorTopIsPosts}"

fun NavController.navigateToCreatorTop(creatorId: FanboxCreatorId, isPosts: Boolean = false) {
    this.navigateWithLog("creatorTop/$creatorId/$isPosts")
}

fun NavGraphBuilder.creatorTopScreen(
    navigateToPostDetail: (FanboxPostId) -> Unit,
    navigateToPostSearch: (String, FanboxCreatorId) -> Unit,
    navigateToBillingPlus: (String?) -> Unit,
    navigateToDownloadAll: (FanboxCreatorId) -> Unit,
    navigateToAlertDialog: (SimpleAlertContents, () -> Unit, () -> Unit) -> Unit,
    terminate: () -> Unit,
) {
    composable(
        route = CreatorTopRoute,
        arguments = listOf(
            navArgument(CreatorTopId) { type = NavType.StringType },
            navArgument(CreatorTopIsPosts) { type = NavType.BoolType },
        ),
    ) {
        CreatorTopRoute(
            modifier = Modifier.fillMaxSize(),
            creatorId = FanboxCreatorId(it.arguments?.getString(CreatorTopId).orEmpty()),
            isPosts = it.arguments?.getBoolean(CreatorTopIsPosts) ?: false,
            navigateToPostDetail = navigateToPostDetail,
            navigateToPostSearch = navigateToPostSearch,
            navigateToBillingPlus = navigateToBillingPlus,
            navigateToDownloadAll = navigateToDownloadAll,
            navigateToAlertDialog = navigateToAlertDialog,
            terminate = terminate,
        )
    }
}
