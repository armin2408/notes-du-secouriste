package com.notesdusecouriste.core.ui.legal

import com.notesdusecouriste.core.ui.R
import java.util.Calendar

/**
 * IDs de copy gardrails partagés (Settings, futur PDF, futur rappel photo).
 * Source de vérité : [R.string] dans `strings_disclaimers.xml`.
 */
object DisclaimerResources {
    val privacyTitle = R.string.guardrail_privacy_title
    val privacyShort = R.string.guardrail_privacy_short
    val privacyPolicyLabel = R.string.guardrail_privacy_policy_label
    val privacyPolicyUrl = R.string.guardrail_privacy_policy_url
    val documentTitle = R.string.guardrail_document_title
    val documentBody = R.string.guardrail_document_body
    val ackButton = R.string.guardrail_ack_button
    val settingsLegalSection = R.string.settings_legal_section
    val photoTitle = R.string.guardrail_photo_title
    val photoBody = R.string.guardrail_photo_body
    val photoAckButton = R.string.guardrail_photo_ack

    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
}
