package com.notesdusecouriste.app.shortcuts

import android.content.Intent

/** Raccourcis d'application déclarés dans `res/xml/shortcuts.xml`. */
enum class AppShortcut(val action: String) {
    NewNote("com.notesdusecouriste.app.action.NEW_NOTE"),
    AideMemoire("com.notesdusecouriste.app.action.AIDE_MEMOIRE"),
    ;

    companion object {
        fun from(intent: Intent?): AppShortcut? =
            intent?.action?.let { action -> entries.firstOrNull { it.action == action } }
    }
}
