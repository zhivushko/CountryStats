    package org.crossplatform.stats.navigation

    import androidx.compose.animation.core.tween
    import androidx.compose.animation.slideInHorizontally
    import androidx.compose.animation.slideOutHorizontally
    import androidx.compose.animation.togetherWith
    import androidx.compose.foundation.layout.RowScope
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Modifier
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation3.runtime.entryProvider
    import androidx.navigation3.ui.NavDisplay
    import org.crossplatform.stats.ui.viewmodel.CountryListViewModel
    import org.crossplatform.stats.data.CountryRepositoryImpl
    import org.crossplatform.stats.ui.viewmodel.CountryDetailedViewModel
    import org.crossplatform.stats.domain.CountryListIntent
    import org.crossplatform.stats.domain.Screen
    import org.crossplatform.stats.ui.screen.CountryDetailScreen
    import org.crossplatform.stats.ui.screen.CountryListScreen
    import org.crossplatform.stats.ui.AppScaffold

    @Composable
    fun AppNavDisplay(
        modifier: Modifier = Modifier,
        actions: @Composable RowScope.() -> Unit = {},
    ) {
        val repository = remember { CountryRepositoryImpl() }
        val navigator: AppViewModel = viewModel { AppViewModel() }
        val listViewModel: CountryListViewModel = viewModel {
            CountryListViewModel(
                navigator = navigator,
                repository = repository,
            )
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
                            onCountryClick = { id ->
                                listViewModel.onIntent(CountryListIntent.CardClicked(id))
                            }
                        )
                    }

                    entry<Screen.Detail> { key ->
                        val detailViewModel =
                            CountryDetailedViewModel(
                                countryId = key.id,
                                repository = repository,
                            )
                        val state by detailViewModel.state.collectAsState()

                        CountryDetailScreen(
                            state = state,
                            onCountryClick = { id ->
                                navigator.addToBackStack(Screen.Detail(id))
                            },
                        )
                    }
                },
                popTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    )
                },
            )
        }
    }
