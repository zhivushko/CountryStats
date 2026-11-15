package org.crossplatform.stats.ui.navigation

sealed interface Screen
{
    data object List: Screen
    data class Detail(val id: Int) : Screen
}