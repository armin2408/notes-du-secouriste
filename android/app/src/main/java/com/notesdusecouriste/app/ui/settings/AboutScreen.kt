package com.notesdusecouriste.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.app.R
import com.notesdusecouriste.core.ui.legal.DisclaimerResources
import com.notesdusecouriste.core.ui.systembars.navigationBarBottomPadding
import com.notesdusecouriste.core.ui.systembars.scaffoldContentWithoutNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val privacyUrl = stringResource(DisclaimerResources.privacyPolicyUrl)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = scaffoldContentWithoutNavigationBar(),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIdentityHeader()

            AboutCard(
                icon = Icons.Outlined.VolunteerActivism,
                title = stringResource(R.string.about_volunteer_title),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = stringResource(R.string.about_volunteer_body),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            AboutCard(
                icon = Icons.Outlined.LocalCafe,
                title = stringResource(R.string.about_coffee_title),
                badge = stringResource(R.string.about_coffee_badge),
            ) {
                Text(
                    text = stringResource(R.string.about_coffee_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FilledTonalButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalCafe,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.about_coffee_action),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            AboutCard(
                icon = Icons.Outlined.PrivacyTip,
                title = stringResource(DisclaimerResources.privacyTitle),
            ) {
                Text(
                    text = stringResource(DisclaimerResources.privacyShort),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
                    },
                ) {
                    Text(stringResource(DisclaimerResources.privacyPolicyLabel))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp),
                    )
                }
            }

            AboutCard(
                icon = Icons.Outlined.Gavel,
                title = stringResource(DisclaimerResources.documentTitle),
            ) {
                Text(
                    text = stringResource(
                        DisclaimerResources.documentBody,
                        DisclaimerResources.currentYear(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AppVersionFooter()
        }
    }
}

@Composable
private fun AppIdentityHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(28.dp)),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorResource(R.color.ic_launcher_background),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground_src),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AboutCard(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }
            content()
        }
    }
}
