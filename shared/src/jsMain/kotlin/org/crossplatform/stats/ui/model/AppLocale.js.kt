package org.crossplatform.stats.ui.model

import web.dom.document

actual fun applyPlatformLocale(locale: String) {
    document.documentElement.setAttribute("lang", locale)
}
