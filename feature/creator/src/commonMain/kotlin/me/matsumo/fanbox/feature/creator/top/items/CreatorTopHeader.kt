package me.matsumo.fanbox.feature.creator.top.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.model.fanbox.FanboxCreatorDetail
import me.matsumo.fanbox.core.ui.MR
import me.matsumo.fanbox.core.ui.component.PixiViewTopBar
import me.matsumo.fanbox.core.ui.component.TagItems
import me.matsumo.fanbox.core.ui.extensition.FadePlaceHolder
import me.matsumo.fanbox.core.ui.extensition.asCoilImage
import me.matsumo.fanbox.core.ui.extensition.fanboxHeader
import me.matsumo.fanbox.core.ui.theme.bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTopHeader(
    creatorDetail: FanboxCreatorDetail,
    onClickTerminate: () -> Unit,
    onClickMenu: () -> Unit,
    onClickLink: (String) -> Unit,
    onClickDescription: (String) -> Unit,
    onClickFollow: suspend (String) -> Result<Unit>,
    onClickUnfollow: suspend (String) -> Result<Unit>,
    onClickSupporting: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val tagItems = createTags(creatorDetail)
    var isFollowed by remember(creatorDetail.isFollowed) { mutableStateOf(creatorDetail.isFollowed) }

    Box(modifier) {
        Column(Modifier.fillMaxWidth()) {
            HeaderTop(
                modifier = Modifier.fillMaxWidth(),
                creatorDetail = creatorDetail,
                isSupported = creatorDetail.isSupported,
                isFollowed = isFollowed,
                onClickSupport = { onClickSupporting.invoke(creatorDetail.supportingBrowserUrl) },
                onClickFollow = {
                    scope.launch {
                        isFollowed = true
                        isFollowed = onClickFollow.invoke(creatorDetail.user.userId).isSuccess
                    }
                },
                onClickUnfollow = {
                    scope.launch {
                        isFollowed = false
                        isFollowed = !onClickUnfollow.invoke(creatorDetail.user.userId).isSuccess
                    }
                },
                onClickLink = { onClickLink.invoke(it) },
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = creatorDetail.user.name,
                style = MaterialTheme.typography.titleLarge.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "@${creatorDetail.creatorId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            DescriptionItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = if (tagItems.isEmpty()) 16.dp else 0.dp,
                    ),
                description = creatorDetail.description,
                onClickShowDescription = onClickDescription,
            )

            if (tagItems.isNotEmpty()) {
                TagItems(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp,
                        ),
                    tags = tagItems.toImmutableList(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    onClickTag = { /* do nothing */ },
                )
            }
        }

        PixiViewTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            isTransparent = true,
            windowInsets = WindowInsets(0, 0, 0, 0),
            onClickNavigation = onClickTerminate,
            onClickActions = onClickMenu,
        )
    }
}

@Composable
private fun FollowStateButton(
    isSupported: Boolean,
    isFollowed: Boolean,
    onClickSupport: () -> Unit,
    onClickFollow: () -> Unit,
    onClickUnfollow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isSupported -> {
            Button(
                modifier = modifier.width(128.dp),
                onClick = onClickSupport,
            ) {
                Text(text = stringResource(MR.strings.common_supporting))
            }
        }

        isFollowed -> {
            OutlinedButton(
                modifier = modifier.width(128.dp),
                onClick = onClickUnfollow,
            ) {
                Text(text = stringResource(MR.strings.common_unfollow))
            }
        }

        else -> {
            Button(
                modifier = modifier.width(128.dp),
                onClick = onClickFollow,
            ) {
                Text(text = stringResource(MR.strings.common_follow))
            }
        }
    }
}

@Composable
private fun ProfileLinkItem(
    profileLinks: ImmutableList<FanboxCreatorDetail.ProfileLink>,
    onClickLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.End,
        ),
    ) {
        for (profileLink in profileLinks.take(3)) {
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = { onClickLink.invoke(profileLink.url) },
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    painter = painterResource(
                        when (profileLink.link) {
                            FanboxCreatorDetail.Platform.BOOTH -> MR.images.vec_booth
                            FanboxCreatorDetail.Platform.FACEBOOK -> MR.images.vec_facebook
                            FanboxCreatorDetail.Platform.FANZA -> MR.images.vec_fanza
                            FanboxCreatorDetail.Platform.INSTAGRAM -> MR.images.vec_instagram
                            FanboxCreatorDetail.Platform.LINE -> MR.images.vec_line
                            FanboxCreatorDetail.Platform.PIXIV -> MR.images.vec_pixiv
                            FanboxCreatorDetail.Platform.TUMBLR -> MR.images.vec_tumblr
                            FanboxCreatorDetail.Platform.TWITTER -> MR.images.vec_twitter
                            FanboxCreatorDetail.Platform.YOUTUBE -> MR.images.vec_youtube
                            FanboxCreatorDetail.Platform.UNKNOWN -> MR.images.vec_unknown_link
                        },
                    ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun HeaderTop(
    creatorDetail: FanboxCreatorDetail,
    isSupported: Boolean,
    isFollowed: Boolean,
    onClickSupport: () -> Unit,
    onClickFollow: () -> Unit,
    onClickUnfollow: () -> Unit,
    onClickLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .layoutId("background")
                    .aspectRatio(5 / 2f),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .fanboxHeader()
                    .data(creatorDetail.coverImageUrl ?: creatorDetail.user.iconUrl)
                    .build(),
                loading = {
                    FadePlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            SubcomposeAsyncImage(
                modifier = Modifier
                    .layoutId("icon")
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                    ),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .fanboxHeader()
                    .error(MR.images.im_default_user.asCoilImage())
                    .data(creatorDetail.user.iconUrl)
                    .build(),
                loading = {
                    FadePlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            ProfileLinkItem(
                modifier = Modifier.layoutId("links"),
                profileLinks = creatorDetail.profileLinks.toImmutableList(),
                onClickLink = onClickLink,
            )

            FollowStateButton(
                modifier = Modifier.layoutId("button"),
                isSupported = isSupported,
                isFollowed = isFollowed,
                onClickSupport = onClickSupport,
                onClickFollow = onClickFollow,
                onClickUnfollow = onClickUnfollow,
            )
        },
        measurePolicy = { measurables, constraints ->
            val iconPlaceable = measurables.find { it.layoutId == "icon" }!!.measure(Constraints.fixed(80.dp.roundToPx(), 80.dp.roundToPx()))
            val backgroundPlaceable = measurables.find { it.layoutId == "background" }!!.measure(constraints)
            val buttonPlaceable = measurables.find { it.layoutId == "button" }!!.measure(Constraints.fixedWidth(128.dp.roundToPx()))
            val linksPlaceable = measurables.find { it.layoutId == "links" }!!.measure(
                Constraints.fixedWidth(constraints.maxWidth - iconPlaceable.width - buttonPlaceable.width - 16.dp.roundToPx() * 4)
            )

            val backgroundPosition = Alignment.TopCenter.align(
                size = IntSize(backgroundPlaceable.width, backgroundPlaceable.height),
                space = IntSize(constraints.maxWidth, constraints.maxHeight),
                layoutDirection,
            )

            val iconPosition = IntOffset(
                x = 16.dp.roundToPx(),
                y = backgroundPlaceable.height - iconPlaceable.height / 2,
            )

            val buttonPosition = IntOffset(
                x = constraints.maxWidth - buttonPlaceable.width - 16.dp.roundToPx(),
                y = backgroundPlaceable.height + 8.dp.roundToPx(),
            )

            val linksPosition = IntOffset(
                x = iconPosition.x + iconPlaceable.width + 16.dp.roundToPx(),
                y = backgroundPlaceable.height + ((buttonPlaceable.height - linksPlaceable.height) / 2) + 8.dp.roundToPx(),
            )

            layout(constraints.maxWidth, buttonPosition.y + buttonPlaceable.height) {
                backgroundPlaceable.place(backgroundPosition)
                iconPlaceable.place(iconPosition)
                buttonPlaceable.place(buttonPosition)
                linksPlaceable.place(linksPosition)
            }
        },
    )
}

@Composable
private fun DescriptionItem(
    description: String,
    onClickShowDescription: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEllipsized by remember(description) { mutableStateOf(false) }

    Row(modifier) {
        Text(
            modifier = Modifier.weight(1f),
            text = description.replace("\n", " "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = {
                isEllipsized = it.hasVisualOverflow
            },
        )

        if (isEllipsized) {
            IconButton(onClick = { onClickShowDescription.invoke(description) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun createTags(creatorDetail: FanboxCreatorDetail): List<String> {
    return mutableListOf<String>().apply {
        if (creatorDetail.isSupported) {
            add(stringResource(MR.strings.creator_tag_is_supported))
        }

        if (creatorDetail.hasAdultContent) {
            add(stringResource(MR.strings.creator_tag_has_adult_content))
        }

        if (creatorDetail.isAcceptingRequest) {
            add(stringResource(MR.strings.creator_tag_is_accepting_request))
        }

        if (creatorDetail.hasBoothShop) {
            add(stringResource(MR.strings.creator_tag_has_booth_shop))
        }
    }
}
