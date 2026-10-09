package org.crossplatform.stats

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.crossplatform.stats.navigation.AppNavDisplay
import org.crossplatform.stats.resources.Res
import org.crossplatform.stats.resources.language
import org.crossplatform.stats.resources.tonality
import org.crossplatform.stats.ui.AppTheme
import org.crossplatform.stats.ui.model.AppLocaleKey
import org.crossplatform.stats.ui.model.toggleLanguage
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    var darkTheme by remember { mutableStateOf(false) }

    AppLocaleKey {
        AppTheme(darkTheme) {
            AppNavDisplay(
                actions = {
                    IconButton(
                        onClick = { darkTheme = !darkTheme },
                    ) {
                        Icon(
                            contentDescription = "",
                            painter = painterResource(Res.drawable.tonality),
                        )
                    }
                    IconButton(
                        onClick = { toggleLanguage() },
                    ) {
                        Icon(
                            contentDescription = "",
                            painter = painterResource(Res.drawable.language),
                        )
                    }
                },
            )
        }
    }
}
