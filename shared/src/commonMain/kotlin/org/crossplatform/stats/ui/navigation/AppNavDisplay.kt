package org.crossplatform.stats.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.crossplatform.stats.data.InMemoryCountryRepository
import org.crossplatform.stats.data.NetworkCountryRepositoryImpl
import org.crossplatform.stats.data.network.WorldBankApi
import org.crossplatform.stats.data.network.createHttpClient
import org.crossplatform.stats.domain.GetCountryInteractor
import org.crossplatform.stats.domain.LoadCountryUseCase
import org.crossplatform.stats.ui.AppScaffold
import org.crossplatform.stats.ui.detail.CountryDetailedViewModel
import org.crossplatform.stats.ui.list.CountryListIntent
import org.crossplatform.stats.ui.list.CountryListViewModel
import org.crossplatform.stats.ui.screen.CountryDetailScreen
import org.crossplatform.stats.ui.screen.CountryListScreen

@Composable
fun AppNavDisplay(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val interactor: GetCountryInteractor = remember {
        val httpClient = createHttpClient()
        val api = WorldBankApi(httpClient)
        val remote = NetworkCountryRepositoryImpl(api)
        val local = InMemoryCountryRepository()
        val loadCountryUseCase = LoadCountryUseCase(remote, pageSize = 15)
        GetCountryInteractor(loadCountryUseCase, remote, local)
    }

    val navigator: AppViewModel = viewModel { AppViewModel() }
    val listViewModel: CountryListViewModel = viewModel {
        CountryListViewModel(navigator = navigator, getCountryInteractor = interactor)
    }
    val detailViewModel: CountryDetailedViewModel = viewModel {
        CountryDetailedViewModel(interactor = interactor)
    }

    val backStack by navigator.backStack.collectAsState()

    AppScaffold(
        modifier = modifier,
        onBack = if (backStack.size > 1) navigator::onBack else null,
        actions = actions,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = navigator::onBack,
            modifier = Modifier.fillMaxSize(),
            entryProvider = entryProvider {
                entry<Screen.List> {
                    val state by listViewModel.state.collectAsState()
                    CountryListScreen(
                        state = state,
                        onCountryClick = { id -> listViewModel.onIntent(CountryListIntent.CardClicked(id)) },
                        onQueryChange = { value -> listViewModel.onIntent(CountryListIntent.QueryChanged(value)) },
                        onRetry = { listViewModel.onIntent(CountryListIntent.Retry) },
                        onLoadMore = { listViewModel.onIntent(CountryListIntent.LoadMoreRequested) },
                    )
                }

                entry<Screen.Detail> { key ->
                    LaunchedEffect(key.id) {
                        detailViewModel.load(key.id)
                    }
                    val state by detailViewModel.state.collectAsState()
                    CountryDetailScreen(
                        state = state,
                        onCountryClick = { id ->
                            navigator.addToBackStack(Screen.Detail(id))
                        },
                        onRetry = { detailViewModel.retry() },
                    )
                }
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith
                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
            },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            },
        )
    }
}
