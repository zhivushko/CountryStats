package org.crossplatform.stats.ui.model

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun applyPlatformLocale(locale: String) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(locale)
    )
}
