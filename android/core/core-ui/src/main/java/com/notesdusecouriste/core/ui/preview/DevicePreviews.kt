package com.notesdusecouriste.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview « responsive » : rend le composable annoté sur plusieurs ratios et
 * résolutions à la fois, sans émulateur. Idéal pour repérer les ruptures de layout.
 *
 * Astuce Android Studio : dans le panneau Preview, on peut saisir une preview et
 * **étirer sa largeur/hauteur à la souris** (Resizable Preview) pour un test continu.
 */
@Preview(
    name = "Compact (360×640)",
    group = "Responsive",
    device = "spec:width=360dp,height=640dp,dpi=480",
    showSystemUi = true,
)
@Preview(
    name = "Téléphone (411×891)",
    group = "Responsive",
    device = "spec:width=411dp,height=891dp,dpi=420",
    showSystemUi = true,
)
@Preview(
    name = "Grand (480×1024)",
    group = "Responsive",
    device = "spec:width=480dp,height=1024dp,dpi=420",
    showSystemUi = true,
)
@Preview(
    name = "Paysage (891×411)",
    group = "Responsive",
    device = "spec:width=891dp,height=411dp,dpi=420",
    showSystemUi = true,
)
@Preview(
    name = "Pliable déplié (673×841)",
    group = "Responsive",
    device = "spec:width=673dp,height=841dp,dpi=420",
    showSystemUi = true,
)
@Preview(
    name = "Tablette (800×1280)",
    group = "Responsive",
    device = "spec:width=800dp,height=1280dp,dpi=240",
    showSystemUi = true,
)
annotation class ResponsivePreviews

/**
 * Multipreview thème clair / sombre. À combiner avec [ResponsivePreviews] sur deux
 * fonctions de preview distinctes, ou à utiliser seul pour les composants isolés.
 */
@Preview(name = "Clair", group = "Thème", showBackground = true)
@Preview(
    name = "Sombre",
    group = "Thème",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
annotation class ThemePreviews

/**
 * Multipreview « grande police » : vérifie la robustesse aux réglages d'accessibilité
 * (texte agrandi), un point sensible en intervention.
 */
@Preview(name = "Police 100 %", group = "Police", showBackground = true, fontScale = 1.0f)
@Preview(name = "Police 130 %", group = "Police", showBackground = true, fontScale = 1.3f)
@Preview(name = "Police 200 %", group = "Police", showBackground = true, fontScale = 2.0f)
annotation class FontScalePreviews
