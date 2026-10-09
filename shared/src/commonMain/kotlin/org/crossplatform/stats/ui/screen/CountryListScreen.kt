package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.domain.CountryListState
import org.crossplatform.stats.ui.components.CountryList
import org.crossplatform.stats.ui.model.toCardsUI

@Composable
fun CountryListScreen(
    state: CountryListState,
    onQueryChange : (String) -> Unit,
    onCountryClick: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            ) {

            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск") },
                singleLine = true,
            )
            Spacer(Modifier.height(16.dp))
            CountryList(
                countries = state.items.toCardsUI(),
                modifier = Modifier.fillMaxWidth(),
                onCountryClick = onCountryClick,
            )
        }
    }
}
