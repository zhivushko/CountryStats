package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.no_results
import org.crossplatform.stats.resources.search
import org.crossplatform.stats.ui.components.CountryCard
import org.crossplatform.stats.ui.components.FullScreenError
import org.crossplatform.stats.ui.components.FullScreenLoading
import org.crossplatform.stats.ui.components.LoadingMoreError
import org.crossplatform.stats.ui.components.LoadingMoreIndicator
import org.crossplatform.stats.ui.list.CountryListState
import org.jetbrains.compose.resources.stringResource

@Composable
fun CountryListScreen(
    state: CountryListState,
    onQueryChange: (String) -> Unit,
    onCountryClick: (String) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    when (state) {
        CountryListState.Loading -> FullScreenLoading(modifier)

        is CountryListState.Error -> FullScreenError(
            message = stringResource(state.messageRes),
            onRetry = onRetry,
            modifier = modifier,
        )

        is CountryListState.Success -> {
            CountryListContent(
                state = state,
                onQueryChange = onQueryChange,
                onCountryClick = onCountryClick,
                onLoadMore = onLoadMore,
                onRetry = onRetry,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CountryListContent(
    state: CountryListState.Success,
    onQueryChange: (String) -> Unit,
    onCountryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(Res.string.search)) },
            singleLine = true,
        )

        if (state.isSearching && state.items.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        } else {
            Spacer(Modifier.height(16.dp))
        }

        if (state.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isSearching) {
                    CircularProgressIndicator()
                } else if (state.loadMoreFailed) {
                    LoadingMoreError(onRetry = onRetry)
                } else {
                    Text(
                        text = stringResource(Res.string.no_results),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()
            val lastVisibleIndex by remember {
                derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            }

            LaunchedEffect(lastVisibleIndex, state.items.size) {
                if (lastVisibleIndex >= state.items.size - 3) {
                    onLoadMore()
                }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.items, key = { it.id }) { country ->
                    CountryCard(
                        country = country,
                        onClick = { onCountryClick(country.id) },
                    )
                }

                if (state.isLoadingMore) {
                    item {
                        LoadingMoreIndicator()
                    }
                }

                if (state.loadMoreFailed) {
                    item {
                        LoadingMoreError(onRetry = onRetry)
                    }
                }
            }
        }
    }
}
