package me.matsumo.fanbox.feature.post.detail.items

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import me.matsumo.fanbox.core.ui.component.AdultContentThumbnail
import me.matsumo.fanbox.core.ui.extensition.LocalFanboxMetadata
import me.matsumo.fankt.fanbox.domain.model.FanboxPostDetail
import sh.calvin.autolinktext.rememberAutoLinkText

internal fun LazyListScope.postDetailImageHeader(
    content: FanboxPostDetail.Body.Image,
    isAdultContents: Boolean,
    isOverrideAdultContents: Boolean,
    isTestUser: Boolean,
    onClickImage: (FanboxPostDetail.ImageItem) -> Unit,
    onClickDownload: (List<FanboxPostDetail.ImageItem>) -> Unit,
) {
    items(content.images) {
        val metadata = LocalFanboxMetadata.current

        if (!isOverrideAdultContents && metadata.context?.user?.showAdultContent == false && isAdultContents) {
            AdultContentThumbnail(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(it.aspectRatio),
                coverImageUrl = it.thumbnailUrl,
                isTestUser = isTestUser,
            )
        } else {
            PostDetailImageItem(
                modifier = Modifier.fillMaxWidth(),
                item = it,
                onClickImage = onClickImage,
                onClickDownload = { onClickDownload.invoke(listOf(it)) },
                onClickAllDownload = { onClickDownload.invoke(content.imageItems) },
            )
        }
    }

    if (content.text.isNotBlank()) {
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = AnnotatedString.rememberAutoLinkText(content.text),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            )
        }
    }
}
