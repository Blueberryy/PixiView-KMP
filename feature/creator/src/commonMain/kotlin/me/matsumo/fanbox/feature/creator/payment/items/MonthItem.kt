package me.matsumo.fanbox.feature.creator.payment.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import me.matsumo.fanbox.core.common.util.format
import me.matsumo.fanbox.core.ui.Res
import me.matsumo.fanbox.core.ui.unit_jpy
import me.matsumo.fanbox.feature.creator.payment.Payment
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MonthItem(
    payments: ImmutableList<Payment>,
    modifier: Modifier = Modifier,
) {
    val samplePaymentDateTime = remember { payments.first().paymentDateTime }
    val paidSum = remember { payments.sumOf { it.paidRecords.sumOf { record -> record.paidAmount } } }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = samplePaymentDateTime.format("MMMM"),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = samplePaymentDateTime.format("yyyy"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(Res.string.unit_jpy, paidSum),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}