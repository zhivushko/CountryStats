package org.crossplatform.stats.ui.model

import java.util.Locale

actual fun applyPlatformLocale(locale: String) {
    Locale.setDefault(Locale.forLanguageTag(locale))
}
