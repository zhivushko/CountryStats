package org.crossplatform.stats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.data.Country
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.country_neighbours
import org.crossplatform.stats.ui.model.CountryCardUI
import org.jetbrains.compose.resources.stringResource

@Composable
fun CountryDetailed(
    modifier: Modifier,
    country: Country,
    neighbours: List<CountryCardUI>,
    onCountryClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        Text(text = country.name, style = MaterialTheme.typography.titleLarge)

        StatBlock(country = country)
        UrbGraphic(country = country)
        if (neighbours.isNotEmpty())
        {
            Column {
                Text(text = stringResource(Res.string.country_neighbours) + ":")
                Spacer(modifier.height(16.dp))
                CountryList(
                    countries = neighbours,
                    onCountryClick = onCountryClick,
                )
            }
        }

    }
}
