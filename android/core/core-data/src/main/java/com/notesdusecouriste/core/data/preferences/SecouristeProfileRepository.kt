package com.notesdusecouriste.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.secouristeProfileDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "secouriste_profile",
)

@Singleton
class SecouristeProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prenomKey = stringPreferencesKey("prenom")
    private val nomKey = stringPreferencesKey("nom")
    private val contactKey = stringPreferencesKey("contact")
    private val organismeKey = stringPreferencesKey("organisme")
    private val competencesKey = stringPreferencesKey("competences")

    val profile: Flow<SecouristeProfile> =
        context.secouristeProfileDataStore.data.map { prefs ->
            SecouristeProfile(
                prenom = prefs[prenomKey].orEmpty(),
                nom = prefs[nomKey].orEmpty(),
                contact = prefs[contactKey].orEmpty(),
                organisme = prefs[organismeKey].orEmpty(),
                competences = prefs[competencesKey].orEmpty(),
            )
        }

    suspend fun save(profile: SecouristeProfile) {
        context.secouristeProfileDataStore.edit { prefs ->
            prefs[prenomKey] = profile.prenom.trim()
            prefs[nomKey] = profile.nom.trim()
            prefs[contactKey] = profile.contact.trim()
            prefs[organismeKey] = profile.organisme.trim()
            prefs[competencesKey] = profile.competences.trim()
        }
    }
}
