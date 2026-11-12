package org.crossplatform.stats.domain

sealed interface Screen
{
    data object List: Screen
    data class Detail(val id: Int) : Screen
}