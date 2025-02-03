package me.matsumo.fanbox.feature.creator.top.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import sh.calvin.autolinktext.rememberAutoLinkText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTopDescriptionDialog(
    description: String,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.78f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                text = AnnotatedString.rememberAutoLinkText(description),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = onDismissRequest,
            ) {
                Text("OK")
            }
        }
    }
}
