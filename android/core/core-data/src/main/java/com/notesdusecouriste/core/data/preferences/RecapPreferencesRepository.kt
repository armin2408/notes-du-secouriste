package com.notesdusecouriste.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.recapDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "recap_preferences",
)

@Singleton
class RecapPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val expandedColumnsKey = booleanPreferencesKey("mesures_columns_expanded")

    /** `false` = colonnes heure compactes (défaut). */
    val mesuresColumnsExpanded: Flow<Boolean> =
        context.recapDataStore.data.map { prefs ->
            prefs[expandedColumnsKey] ?: false
        }

    suspend fun setMesuresColumnsExpanded(expanded: Boolean) {
        context.recapDataStore.edit { prefs ->
            prefs[expandedColumnsKey] = expanded
        }
    }
}
