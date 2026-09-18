package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class NoteSectionKind {
    Victime,
    Mesures,
    Questionnaires,
    Commentaire,
    Photos,
    Respiration,
    Circulation,
    Conscience,
    Sample,
    Opqrst,
    SuspicionAvc,
}

fun NoteSectionKind.icon(): ImageVector = when (this) {
    NoteSectionKind.Victime -> Icons.Outlined.Person
    NoteSectionKind.Mesures -> Icons.Outlined.MonitorHeart
    NoteSectionKind.Questionnaires -> Icons.Outlined.MedicalServices
    NoteSectionKind.Commentaire -> Icons.Outlined.Chat
    NoteSectionKind.Photos -> Icons.Outlined.PhotoCamera
    NoteSectionKind.Respiration -> Icons.Outlined.Air
    NoteSectionKind.Circulation -> Icons.Outlined.FavoriteBorder
    NoteSectionKind.Conscience -> Icons.Outlined.Psychology
    NoteSectionKind.Sample -> Icons.Outlined.MedicalServices
    NoteSectionKind.Opqrst -> Icons.Outlined.MedicalServices
    NoteSectionKind.SuspicionAvc -> Icons.Outlined.Warning
}

val AddTabIcon = Icons.Outlined.Add
