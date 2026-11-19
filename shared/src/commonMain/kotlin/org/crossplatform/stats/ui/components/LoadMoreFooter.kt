package org.crossplatform.stats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadMoreFooter(
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
            isLoading -> CircularProgressIndicator()
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
                // Автоподгрузка: когда футер появился на экране — просим следующую страницу
                LaunchedEffect(Unit) { onLoadMore() }
                CircularProgressIndicator()
            }
        }
    }
}