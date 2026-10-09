package org.crossplatform.stats.ui.model

import platform.Foundation.NSUserDefaults

actual fun applyPlatformLocale(locale: String) {
    NSUserDefaults.standardUserDefaults
        .setObject(listOf(locale), forKey = "AppleLanguages")
}
