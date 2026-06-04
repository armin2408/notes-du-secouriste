package com.notesdusecouriste.feature.interventionnotes.ui

import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceOption
import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceTone

object MesureChoices {
    val respirationAmplitude = listOf(
        ChoiceOption("normale", "Normale", ChoiceTone.Green),
        ChoiceOption("forte", "Forte", ChoiceTone.Yellow),
        ChoiceOption("faible", "Faible", ChoiceTone.Orange),
    )
    val respirationRegularite = listOf(
        ChoiceOption("regulier", "Régulier", ChoiceTone.Green),
        ChoiceOption("irregulier", "Irrégulier", ChoiceTone.Yellow),
    )
    val respirationAspect = listOf(
        ChoiceOption("normal", "Normal", ChoiceTone.Green),
        ChoiceOption("violace", "Violacé", ChoiceTone.Orange),
        ChoiceOption("pale", "Pâle", ChoiceTone.Orange),
        ChoiceOption("autre", "Autre", ChoiceTone.Orange),
    )
    val circulationAmplitude = listOf(
        ChoiceOption("frappe", "Frappé", ChoiceTone.Green),
        ChoiceOption("filant", "Filant", ChoiceTone.Orange),
    )
    val circulationRegularite = listOf(
        ChoiceOption("regulier", "Régulier", ChoiceTone.Green),
        ChoiceOption("irregulier", "Irrégulier", ChoiceTone.Yellow),
    )
    val circulationAspect = listOf(
        ChoiceOption("normal", "Normal", ChoiceTone.Green),
        ChoiceOption("anormal", "Anormal", ChoiceTone.Orange),
    )
    val trc = listOf(
        ChoiceOption("inf_2", "inf. 2 sec.", ChoiceTone.Green),
        ChoiceOption("sup_2", "sup. 2 sec.", ChoiceTone.Orange),
    )
    val conscience = listOf(
        ChoiceOption("conscient", "Conscient", ChoiceTone.Green),
        ChoiceOption("inconscient", "Inconscient", ChoiceTone.Orange),
    )
    val ouiNon = listOf(
        ChoiceOption("oui", "Oui", ChoiceTone.Green),
        ChoiceOption("non", "Non", ChoiceTone.Orange),
    )
    val propos = listOf(
        ChoiceOption("coherent", "Cohérent", ChoiceTone.Green),
        ChoiceOption("incoherent", "Incohérents", ChoiceTone.Orange),
    )
    val visage = listOf(
        ChoiceOption("symetrique", "Visage symétrique", ChoiceTone.Green),
        ChoiceOption("asymetrique", "Visage asymétrique", ChoiceTone.Orange),
    )
    val motricite = listOf(
        ChoiceOption("symetrique", "Symétrique", ChoiceTone.Green),
        ChoiceOption("asymetrique", "Asymétrique", ChoiceTone.Orange),
    )
    val parole = listOf(
        ChoiceOption("normale", "Normale", ChoiceTone.Green),
        ChoiceOption("confuse", "Confuse", ChoiceTone.Orange),
    )
}
