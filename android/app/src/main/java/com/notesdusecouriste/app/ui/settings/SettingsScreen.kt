package com.notesdusecouriste.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.app.R
import com.notesdusecouriste.app.ui.theme.AppThemeViewModel
import com.notesdusecouriste.app.ui.theme.appThemeViewModel
import com.notesdusecouriste.core.data.preferences.SecouristeProfile
import com.notesdusecouriste.core.data.preferences.ThemeDefaultPolicy
import com.notesdusecouriste.core.ui.legal.DisclaimerResources
import com.notesdusecouriste.core.ui.systembars.navigationBarBottomPadding
import com.notesdusecouriste.core.ui.systembars.scaffoldContentWithoutNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenWelcome: () -> Unit = {},
    onOpenChangelog: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    themeViewModel: AppThemeViewModel = appThemeViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val preferences by themeViewModel.preferences.collectAsStateWithLifecycle()
    val profile by settingsViewModel.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val privacyUrl = stringResource(DisclaimerResources.privacyPolicyUrl)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = scaffoldContentWithoutNavigationBar(),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = navigationBarBottomPadding()),
        ) {
            ProfileHeroTile(profile = profile, onClick = onOpenProfile)

            SettingsSectionHeader(stringResource(R.string.settings_section_preferences))
            ThemeStartupTile(
                policy = preferences.defaultPolicy,
                onPolicyChange = themeViewModel::setDefaultPolicy,
            )

            SettingsSectionHeader(stringResource(R.string.settings_section_discover))
            SettingsSegmentedGroup {
                SettingsSegmentedItem(
                    icon = Icons.Outlined.WavingHand,
                    title = stringResource(R.string.settings_welcome_entry),
                    description = stringResource(R.string.settings_welcome_entry_desc),
                    index = 0,
                    count = 2,
                    onClick = onOpenWelcome,
                    trailingIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                )
                SettingsSegmentedItem(
                    icon = Icons.Outlined.History,
                    title = stringResource(R.string.settings_changelog_entry),
                    description = stringResource(R.string.settings_changelog_entry_desc),
                    index = 1,
                    count = 2,
                    onClick = onOpenChangelog,
                    trailingIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                )
            }

            SettingsSectionHeader(stringResource(R.string.settings_section_information))
            SettingsSegmentedGroup {
                SettingsSegmentedItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = stringResource(R.string.settings_privacy_entry),
                    description = stringResource(R.string.settings_privacy_entry_desc),
                    index = 0,
                    count = 2,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
                    },
                    trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                )
                SettingsSegmentedItem(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.settings_about_entry),
                    description = stringResource(R.string.settings_about_entry_desc),
                    index = 1,
                    count = 2,
                    onClick = onOpenAbout,
                    trailingIcon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                )
            }

            AppVersionFooter()
        }
    }
}

@Composable
private fun ProfileHeroTile(profile: SecouristeProfile, onClick: () -> Unit) {
    val name = profile.displayName()
    val initials = listOf(profile.prenom, profile.nom)
        .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
        .joinToString("")
    val subtitle = profile.organisme.trim().ifEmpty {
        stringResource(R.string.settings_profile_entry_desc)
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (initials.isNotEmpty()) {
                        Text(text = initials, style = MaterialTheme.typography.headlineSmall)
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_profile_entry),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = name.ifEmpty { stringResource(R.string.settings_profile_cta) },
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (name.isEmpty()) stringResource(R.string.settings_profile_empty) else subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeStartupTile(
    policy: ThemeDefaultPolicy,
    onPolicyChange: (ThemeDefaultPolicy) -> Unit,
) {
    val policies = listOf(ThemeDefaultPolicy.SYSTEM, ThemeDefaultPolicy.LAST_CHOSEN)
    val selectedIndex = policies.indexOf(policy).coerceAtLeast(0)
    val description = when (policy) {
        ThemeDefaultPolicy.SYSTEM -> stringResource(R.string.settings_theme_system_desc)
        else -> stringResource(R.string.settings_theme_last_chosen_desc)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SettingsLeadingIcon(Icons.Outlined.Palette)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_theme_section),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            ConnectedChoiceButtons(
                options = listOf(
                    stringResource(R.string.settings_theme_system_short),
                    stringResource(R.string.settings_theme_last_chosen_short),
                ),
                selectedIndex = selectedIndex,
                onSelect = { onPolicyChange(policies[it]) },
            )
            Text(
                text = stringResource(R.string.settings_theme_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
