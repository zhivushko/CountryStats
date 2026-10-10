package org.crossplatform.stats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.no_results
import org.crossplatform.stats.ui.model.CountryCardUI
import org.jetbrains.compose.resources.stringResource


@Composable
fun CountryList(
    countries: List<CountryCardUI>,
    modifier: Modifier = Modifier,
    onCountryClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (countries.isEmpty()) {
            Text(text = stringResource(Res.string.no_results),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        countries.forEach { country ->
            CountryCard(
                country = country,
                onClick = { onCountryClick(country.id) },
            )
        }
    }
}
