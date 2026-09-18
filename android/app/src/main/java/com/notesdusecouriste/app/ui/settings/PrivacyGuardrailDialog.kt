package com.notesdusecouriste.app.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.core.ui.legal.DisclaimerResources

/**
 * Affiche une fois le rappel confidentialité (court) jusqu’à acknowledgement DataStore.
 */
@Composable
fun PrivacyGuardrailDialog(
    viewModel: GuardrailsViewModel = hiltViewModel(),
) {
    val acknowledged by viewModel.privacyShortAcknowledged.collectAsStateWithLifecycle()
    if (acknowledged) return

    val context = LocalContext.current
    val privacyUrl = stringResource(DisclaimerResources.privacyPolicyUrl)

    AlertDialog(
        onDismissRequest = { /* obligatoire : lire puis Compris */ },
        title = { Text(stringResource(DisclaimerResources.privacyTitle)) },
        text = {
            Text(stringResource(DisclaimerResources.privacyShort))
        },
        confirmButton = {
            TextButton(onClick = viewModel::acknowledgePrivacyShort) {
                Text(stringResource(DisclaimerResources.ackButton))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
                },
            ) {
                Text(stringResource(DisclaimerResources.privacyPolicyLabel))
            }
        },
    )
}
