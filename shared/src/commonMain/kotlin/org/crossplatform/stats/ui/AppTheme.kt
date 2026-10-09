package org.crossplatform.stats.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val lightColors = lightColorScheme(
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF1976D2),
    background = Color(0xFFF5F5F5)
)
val darkColors = darkColorScheme(
    surface = Color(0xFF1E1E1E),
    primary = Color(0xFF90CAF9),
    background = Color(0xFF121212)
)
@Composable
fun AppTheme(darkTheme: Boolean,
             content: @Composable () -> Unit)
{
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors  else lightColors,
        content = content
    )
}