package com.xavierarbat.xavierarbatback.commons

import java.util.Locale
import kotlin.text.split

object LocaleUtils {

    const val DEFAULT_LANGUAGE_CODE = "en"

    internal fun parseLang(acceptLanguage: String): String =
        Locale.LanguageRange.parse(acceptLanguage)
            .firstOrNull()
            ?.range
            ?.split("-")
            ?.first()
            ?: DEFAULT_LANGUAGE_CODE
}