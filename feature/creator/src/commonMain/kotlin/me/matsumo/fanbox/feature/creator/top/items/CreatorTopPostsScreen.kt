package me.matsumo.fanbox.feature.creator.top.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList
import me.matsumo.fanbox.core.model.UserData
import me.matsumo.fanbox.core.model.fanbox.FanboxCreatorTag
import me.matsumo.fanbox.core.model.fanbox.FanboxPost
import me.matsumo.fanbox.core.model.fanbox.id.CreatorId
import me.matsumo.fanbox.core.model.fanbox.id.PostId
import me.matsumo.fanbox.core.ui.ads.BannerAdView
import me.matsumo.fanbox.core.ui.ads.NativeAdView
import me.matsumo.fanbox.core.ui.component.PostGridItem
import me.matsumo.fanbox.core.ui.component.PostItem
import me.matsumo.fanbox.core.ui.extensition.FadePlaceHolder
import me.matsumo.fanbox.core.ui.extensition.Platform
import me.matsumo.fanbox.core.ui.extensition.currentPlatform
import me.matsumo.fanbox.core.ui.extensition.drawVerticalScrollbar
import me.matsumo.fanbox.core.ui.extensition.fanboxHeader
import me.matsumo.fanbox.core.ui.theme.bold
import me.matsumo.fanbox.core.ui.view.PagingErrorSection

@Composable
internal fun CreatorTopPostsScreen(
    listState: LazyListState,
    gridState: LazyGridState,
    userData: UserData,
    bookmarkedPosts: ImmutableList<PostId>,
    pagingAdapter: LazyPagingItems<FanboxPost>,
    creatorTags: ImmutableList<FanboxCreatorTag>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickTag: (FanboxCreatorTag) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (userData.isUseGridMode) {
        GridSection(
            modifier = modifier,
            state = gridState,
            userData = userData,
            bookmarkedPosts = bookmarkedPosts,
            pagingAdapter = pagingAdapter,
            creatorTags = creatorTags,
            onClickPost = onClickPost,
            onClickTag = onClickTag,
        )
    } else {
        ColumnSection(
            modifier = modifier,
            state = listState,
            userData = userData,
            bookmarkedPosts = bookmarkedPosts,
            pagingAdapter = pagingAdapter,
            creatorTags = creatorTags,
            onClickPost = onClickPost,
            onClickPostBookmark = onClickPostBookmark,
            onClickCreator = onClickCreator,
            onClickTag = onClickTag,
            onClickPostLike = onClickPostLike,
            onClickPlanList = onClickPlanList,
        )
    }
}

@Composable
private fun ColumnSection(
    state: LazyListState,
    userData: UserData,
    bookmarkedPosts: ImmutableList<PostId>,
    pagingAdapter: LazyPagingItems<FanboxPost>,
    creatorTags: ImmutableList<FanboxCreatorTag>,
    onClickPost: (PostId) -> Unit,
    onClickPostBookmark: (FanboxPost, Boolean) -> Unit,
    onClickCreator: (CreatorId) -> Unit,
    onClickTag: (FanboxCreatorTag) -> Unit,
    onClickPostLike: (PostId) -> Unit,
    onClickPlanList: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val adOffset: Int
    val adInterval: Int

    if (currentPlatform == Platform.Android) {
        adOffset = 3
        adInterval = 4
    } else {
        adOffset = 1
        adInterval = 3
    }

    LazyColumn(
        modifier = modifier.drawVerticalScrollbar(state),
        state = state,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (creatorTags.isNotEmpty()) {
            item {
                LazyRow(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(creatorTags) {
                        TagItem(
                            tag = it,
                            onClickTag = onClickTag,
                        )
                    }
                }
            }
        }

        items(
            count = pagingAdapter.itemCount,
            key = pagingAdapter.itemKey { it.id.uniqueValue },
            contentType = pagingAdapter.itemContentType(),
        ) { index ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                pagingAdapter[index]?.let { post ->
                    PostItem(
                        modifier = Modifier.fillMaxWidth(),
                        post = post.copy(isBookmarked = bookmarkedPosts.contains(post.id)),
                        isHideAdultContents = userData.isHideAdultContents,
                        isOverrideAdultContents = userData.isAllowedShowAdultContents,
                        isTestUser = userData.isTestUser,
                        onClickPost = { if (!post.isRestricted) onClickPost.invoke(it) },
                        onClickCreator = onClickCreator,
                        onClickPlanList = onClickPlanList,
                        onClickLike = onClickPostLike,
                        onClickBookmark = { _, isBookmarked ->
                            onClickPostBookmark.invoke(post, isBookmarked)
                        },
                    )
                }

                if ((index + adOffset) % adInterval == 0 && !userData.hasPrivilege) {
                    if (currentPlatform == Platform.IOS) {
                        BannerAdView(modifier = Modifier.fillMaxWidth())
                    } else {
                        NativeAdView(
                            modifier = Modifier.fillMaxWidth(),
                            key = "$index",
                        )
                    }
                }
            }
        }

        if (pagingAdapter.loadState.append is LoadState.Error) {
            item {
                PagingErrorSection(
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { pagingAdapter.retry() },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun GridSection(
    state: LazyGridState,
    userData: UserData,
    bookmarkedPosts: ImmutableList<PostId>,
    pagingAdapter: LazyPagingItems<FanboxPost>,
    creatorTags: ImmutableList<FanboxCreatorTag>,
    onClickPost: (PostId) -> Unit,
    onClickTag: (FanboxCreatorTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (pagingAdapter.loadState.append !is LoadState.Error) {
        LazyVerticalGrid(
            modifier = modifier
                .drawVerticalScrollbar(state, spanCount = 2)
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            state = state,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (creatorTags.isNotEmpty()) {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    LazyRow(
                        modifier = Modifier
                            .height(112.dp)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(creatorTags) {
                            TagItem(
                                tag = it,
                                onClickTag = onClickTag,
                            )
                        }
                    }
                }
            }

            items(
                count = pagingAdapter.itemCount,
                key = pagingAdapter.itemKey { it.id.uniqueValue },
                contentType = pagingAdapter.itemContentType(),
            ) { index ->
                pagingAdapter[index]?.let { post ->
                    PostGridItem(
                        modifier = Modifier.fillMaxWidth(),
                        post = post.copy(isBookmarked = bookmarkedPosts.contains(post.id)),
                        isHideAdultContents = userData.isHideAdultContents,
                        isOverrideAdultContents = userData.isAllowedShowAdultContents,
                        onClickPost = { if (!post.isRestricted) onClickPost.invoke(it) },
                    )
                }
            }
        }
    } else {
        PagingErrorSection(
            modifier = Modifier.fillMaxSize(),
            onRetry = { pagingAdapter.retry() },
        )
    }
}

@Composable
private fun TagItem(
    tag: FanboxCreatorTag,
    onClickTag: (FanboxCreatorTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(5 / 2f, true)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickTag.invoke(tag) },
    ) {
        if (tag.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .fanboxHeader()
                    .data(tag.coverImageUrl)
                    .build(),
                loading = {
                    FadePlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = "#${tag.name}",
                style = MaterialTheme.typography.bodyLarge.bold().copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 3f,
                    ),
                ),
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
            )

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Text(
                    modifier = Modifier.padding(8.dp, 6.dp),
                    text = tag.count.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
