package org.crossplatform.stats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.domain.model.Country
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.country_capital
import org.crossplatform.stats.resources.country_code
import org.crossplatform.stats.resources.country_gdp
import org.crossplatform.stats.resources.country_popularity
import org.crossplatform.stats.resources.country_region
import org.crossplatform.stats.resources.currency_unit
import org.crossplatform.stats.resources.label_with_colon
import org.crossplatform.stats.resources.people_unit
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatBlock(
    modifier: Modifier = Modifier,
    country: Country,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        StatIndicator(
            statName = Res.string.country_code,
            value = country.code,
        )
        StatIndicator(
            statName = Res.string.country_capital,
            value = country.capital,
        )
        StatIndicator(
            statName = Res.string.country_popularity,
            value = "${country.popularity.formatThousand()} ${stringResource(Res.string.people_unit)}",
        )
        StatIndicator(
            statName = Res.string.country_region,
            value = country.region,
        )
        StatIndicator(
            statName = Res.string.country_gdp,
            value = "${country.GDP.formatThousand()} ${stringResource(Res.string.currency_unit)}",
        )
    }
}

@Composable
fun StatIndicator(
    statName: StringResource,
    value: String,
) {
    Row {
        Text(
            text = stringResource(Res.string.label_with_colon, stringResource(statName)),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(10.dp))
        Text(text = value)
    }
}
