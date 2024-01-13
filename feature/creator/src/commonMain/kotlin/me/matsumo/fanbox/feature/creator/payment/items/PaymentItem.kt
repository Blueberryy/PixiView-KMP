package me.matsumo.fanbox.feature.creator.payment.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import me.matsumo.fanbox.core.common.util.format
import me.matsumo.fanbox.core.model.fanbox.FanboxPaidRecord
import me.matsumo.fanbox.core.model.fanbox.PaymentMethod
import me.matsumo.fanbox.core.model.fanbox.id.CreatorId
import me.matsumo.fanbox.core.ui.MR
import me.matsumo.fanbox.core.ui.extensition.asCoilImage
import me.matsumo.fanbox.core.ui.theme.bold
import me.matsumo.fanbox.feature.creator.payment.Payment

@Composable
internal fun PaymentItem(
    payment: Payment,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
        ) {
            TitleItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                dateTime = payment.paymentDateTime,
                totalPaidAmount = payment.paidRecords.sumOf { it.paidAmount }
            )

            Spacer(modifier = Modifier.height(8.dp))

            for (item in payment.paidRecords) {
                PaidItem(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 16.dp,
                        )
                        .fillMaxWidth(),
                    paidRecord = item,
                    onClickCreator = onClickCreator,
                )
            }
        }
    }
}

@Composable
private fun TitleItem(
    dateTime: Instant,
    totalPaidAmount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dateTime.format("yyyy"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = dateTime.format("MMMdd"),
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = stringResource(MR.strings.unit_jpy, totalPaidAmount),
            style = MaterialTheme.typography.titleMedium.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PaidItem(
    paidRecord: FanboxPaidRecord,
    onClickCreator: (CreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClickCreator.invoke(paidRecord.creator.user.creatorId) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .error(MR.images.im_default_user.asCoilImage())
                    .data(paidRecord.creator.user.iconUrl)
                    .build(),
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = paidRecord.creator.user.name,
                    style = MaterialTheme.typography.bodyMedium.bold(),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (paidRecord.paymentMethod) {
                        PaymentMethod.CARD -> stringResource(MR.strings.creator_supporting_payment_method_card)
                        PaymentMethod.PAYPAL -> stringResource(MR.strings.creator_supporting_payment_method_paypal)
                        PaymentMethod.CVS -> stringResource(MR.strings.creator_supporting_payment_method_cvs)
                        PaymentMethod.UNKNOWN -> stringResource(MR.strings.creator_supporting_payment_method_unknown)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            modifier = Modifier.padding(6.dp, 4.dp),
            text = stringResource(MR.strings.unit_jpy, paidRecord.paidAmount),
            style = MaterialTheme.typography.bodyMedium.bold(),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
