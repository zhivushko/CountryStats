package org.crossplatform.stats.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.error_server
import org.crossplatform.stats.resources.retry
import org.crossplatform.stats.resources.search
import org.crossplatform.stats.ui.components.CountryList
import org.crossplatform.stats.ui.components.FullScreenError
import org.crossplatform.stats.ui.components.FullScreenLoading
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

        is CountryListState.Success -> Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(Res.string.search)) },
                    singleLine = true,
                )
                Spacer(Modifier.height(16.dp))
                CountryList(
                    countries = state.items,
                    modifier = Modifier.fillMaxWidth(),
                    onCountryClick = onCountryClick,
                )

                if (state.hasMore || state.isLoadingMore) {
                    LoadMoreFooter(
                        isLoading = state.isLoadingMore,
                        loadMoreFailed = state.loadMoreFailed,
                        onLoadMore = onLoadMore,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadMoreFooter(
    isLoading: Boolean,
    loadMoreFailed: Boolean,
    onLoadMore: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isLoading -> FullScreenLoading(modifier = Modifier.height(48.dp))

            loadMoreFailed -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = stringResource(Res.string.error_server))
                TextButton(onClick = onLoadMore) {
                    Text(text = stringResource(Res.string.retry))
                }
            }

            else -> {
                LaunchedEffect(Unit) { onLoadMore() }
                FullScreenLoading(modifier = Modifier.height(48.dp))
            }
        }
    }
}