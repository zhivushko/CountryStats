package org.crossplatform.stats.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.crossplatform.ui.components.CardNum
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.attach_money
import org.crossplatform.stats.resources.emoji_people
import org.crossplatform.stats.ui.model.CountryCardUI
import org.jetbrains.compose.resources.painterResource

@Composable
fun CountryCard(
    country: CountryCardUI,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    CardSurface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CardNum(country.id)
                Text(text = country.code)
                Text(text = country.name)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CardStat(
                    icon = painterResource(Res.drawable.emoji_people),
                    value = country.popularity,
                )
                CardStat(
                    icon = painterResource(Res.drawable.attach_money),
                    value = country.GDP,
                )
            }
        }
    }
}
