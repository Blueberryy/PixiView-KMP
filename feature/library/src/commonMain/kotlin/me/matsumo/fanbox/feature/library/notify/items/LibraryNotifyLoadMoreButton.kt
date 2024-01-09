package me.matsumo.fanbox.feature.library.notify.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import me.matsumo.fanbox.core.ui.MR

@Composable
internal fun LibraryNotifyLoadMoreButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            onClick = onClick,
        ) {
            Text(
                text = stringResource(MR.strings.notify_load_more),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
