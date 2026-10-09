package org.crossplatform.stats.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.crossplatform.stats.domain.Screen

class AppViewModel : ViewModel() {

    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.List))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    val current: Screen
        get() = _backStack.value.last()

    val canGoBack: Boolean
        get() = _backStack.value.size > 1

    fun addToBackStack(screen: Screen)
    {
        _backStack.update {it + screen}
    }

    fun onBack() {
        _backStack.update { stack ->
            if (stack.size > 1) stack.dropLast(1) else stack
        }
    }
}