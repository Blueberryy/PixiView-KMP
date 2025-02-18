package me.matsumo.fanbox.feature.creator.support.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import me.matsumo.fanbox.core.resources.Res
import me.matsumo.fanbox.core.resources.creator_supporting_fan_card
import me.matsumo.fanbox.core.resources.creator_supporting_payment_method_card
import me.matsumo.fanbox.core.resources.creator_supporting_payment_method_cvs
import me.matsumo.fanbox.core.resources.creator_supporting_payment_method_paypal
import me.matsumo.fanbox.core.resources.creator_supporting_payment_method_unknown
import me.matsumo.fanbox.core.resources.creator_supporting_plan_detail
import me.matsumo.fanbox.core.resources.im_default_user
import me.matsumo.fanbox.core.resources.unit_jpy
import me.matsumo.fanbox.core.ui.extensition.SimmerPlaceHolder
import me.matsumo.fanbox.core.ui.extensition.asCoilImage
import me.matsumo.fanbox.core.ui.extensition.fanboxHeader
import me.matsumo.fanbox.core.ui.theme.bold
import me.matsumo.fankt.fanbox.domain.model.FanboxCreatorPlan
import me.matsumo.fankt.fanbox.domain.model.FanboxPaymentMethod
import me.matsumo.fankt.fanbox.domain.model.id.FanboxCreatorId
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SupportingCreatorItem(
    supportingPlan: FanboxCreatorPlan,
    onClickPlanDetail: (String) -> Unit,
    onClickFanCard: (FanboxCreatorId) -> Unit,
    onClickCreatorPosts: (FanboxCreatorId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { supportingPlan.user?.creatorId?.let(onClickCreatorPosts) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        if (supportingPlan.coverImageUrl != null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(4.dp)),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .fanboxHeader()
                    .data(supportingPlan.coverImageUrl)
                    .build(),
                loading = {
                    SimmerPlaceHolder()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UserSection(
                modifier = Modifier.fillMaxWidth(),
                plan = supportingPlan,
                onClickCreator = onClickCreatorPosts,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = supportingPlan.title,
                style = MaterialTheme.typography.titleMedium.bold(),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = supportingPlan.description.trimEnd(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { supportingPlan.user?.creatorId?.let(onClickFanCard) },
                ) {
                    Text(text = stringResource(Res.string.creator_supporting_fan_card))
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onClickPlanDetail.invoke(supportingPlan.supportingBrowserUrl) },
                ) {
                    Text(text = stringResource(Res.string.creator_supporting_plan_detail))
                }
            }
        }
    }
}

@Composable
private fun UserSection(
    plan: FanboxCreatorPlan,
    onClickCreator: (FanboxCreatorId) -> Unit,
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
                .clickable { plan.user?.creatorId?.let(onClickCreator) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .error(Res.drawable.im_default_user.asCoilImage())
                    .data(plan.user?.iconUrl)
                    .build(),
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = plan.user?.name.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium.bold(),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (plan.paymentMethod) {
                        FanboxPaymentMethod.CARD -> stringResource(Res.string.creator_supporting_payment_method_card)
                        FanboxPaymentMethod.PAYPAL -> stringResource(Res.string.creator_supporting_payment_method_paypal)
                        FanboxPaymentMethod.CVS -> stringResource(Res.string.creator_supporting_payment_method_cvs)
                        FanboxPaymentMethod.UNKNOWN -> stringResource(Res.string.creator_supporting_payment_method_unknown)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Text(
                modifier = Modifier.padding(6.dp, 4.dp),
                text = stringResource(Res.string.unit_jpy, plan.fee),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
