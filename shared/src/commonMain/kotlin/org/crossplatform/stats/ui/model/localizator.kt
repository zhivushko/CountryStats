package org.crossplatform.stats.ui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String) {
    RU("ru"),
    EN("en");

    fun toggle(): AppLanguage = if (this == RU) EN else RU
}

var appLanguage: AppLanguage by mutableStateOf(AppLanguage.RU)
    private set

fun toggleLanguage() {
    appLanguage = appLanguage.toggle()
    applyPlatformLocale(appLanguage.code)
}

expect fun applyPlatformLocale(locale: String)

@Composable
fun AppLocaleKey(content: @Composable () -> Unit) {
    key(appLanguage) {
        content()
    }
}
