package com.notesdusecouriste.app.ui.onboarding

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.preferences.OnboardingPreferencesRepository
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingGateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onboardingPreferences: OnboardingPreferencesRepository,
    private val interventionRepository: InterventionRepository,
) : ViewModel() {

    val currentVersionCode: Int = readVersionCode(context)

    private val migrationReady = MutableStateFlow(false)

    /** null = chargement / migration en cours. */
    val onboardingCompleted: StateFlow<Boolean?> =
        combine(
            migrationReady,
            onboardingPreferences.onboardingCompleted,
        ) { ready, completed ->
            if (!ready) null else completed
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            onboardingPreferences.migrateLegacyUsersIfNeeded(currentVersionCode) {
                interventionRepository.observeInterventions().first().isNotEmpty()
            }
            if (onboardingPreferences.onboardingCompleted.first()) {
                onboardingPreferences.markVersionSeen(currentVersionCode)
            }
            migrationReady.value = true
        }
    }

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            onboardingPreferences.completeOnboarding(currentVersionCode)
            onDone()
        }
    }

    companion object {
        fun readVersionCode(context: Context): Int {
            val info = if (Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            return if (Build.VERSION.SDK_INT >= 28) {
                info.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                info.versionCode
            }
        }
    }
}
