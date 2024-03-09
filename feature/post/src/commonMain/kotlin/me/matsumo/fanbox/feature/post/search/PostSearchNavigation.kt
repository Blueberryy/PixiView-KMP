package me.matsumo.fanbox.feature.post.search

import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLPathPart
import me.matsumo.fanbox.core.model.fanbox.id.CreatorId
import me.matsumo.fanbox.core.model.fanbox.id.PostId
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path

const val PostSearchQueryStr = "postSearchQuery"
const val PostSearchRoute = "postSearch/{$PostSearchQueryStr}"

fun Navigator.navigateToPostSearch(creatorId: CreatorId? = null, creatorQuery: String? = null, tag: String? = null) {
    val query = buildQuery(creatorId, creatorQuery, tag).encodeURLPathPart()
    val encodedQuery = query.encodeURLPathPart()
    val route = if (parseQuery(query).mode != PostSearchMode.Unknown) "postSearch/$encodedQuery" else "postSearch/pixiViewUnknown"

    this.navigate(route)
}

fun RouteBuilder.postSearchScreen(
    navigateToPostSearch: (CreatorId?, String?, String?) -> Unit,
    navigateToPostDetail: (PostId) -> Unit,
    navigateToCreatorPosts: (CreatorId) -> Unit,
    navigateToCreatorPlans: (CreatorId) -> Unit,
    terminate: () -> Unit,
) {
    scene(PostSearchRoute) {
        var query = it.path<String>(PostSearchQueryStr)?.decodeURLPart().orEmpty()

        if (query == "pixiViewUnknown") {
            query = ""
        }

        PostSearchRoute(
            query = query,
            navigateToPostSearch = navigateToPostSearch,
            navigateToPostDetail = navigateToPostDetail,
            navigateToCreatorPosts = navigateToCreatorPosts,
            navigateToCreatorPlans = navigateToCreatorPlans,
            terminate = terminate,
        )
    }
}
