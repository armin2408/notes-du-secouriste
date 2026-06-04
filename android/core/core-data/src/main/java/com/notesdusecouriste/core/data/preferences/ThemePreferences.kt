package com.notesdusecouriste.core.data.preferences

enum class ThemeDefaultPolicy(val storageValue: String) {
    SYSTEM("system"),
    LAST_CHOSEN("last_chosen"),
    ;

    companion object {
        fun fromStorage(value: String): ThemeDefaultPolicy =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}

enum class ExplicitTheme(val storageValue: String) {
    LIGHT("light"),
    DARK("dark"),
    ;

    companion object {
        fun fromStorage(value: String): ExplicitTheme =
            entries.firstOrNull { it.storageValue == value } ?: DARK
    }
}

data class ThemePreferences(
    val defaultPolicy: ThemeDefaultPolicy = ThemeDefaultPolicy.SYSTEM,
    val lastExplicitTheme: ExplicitTheme = ExplicitTheme.DARK,
)
