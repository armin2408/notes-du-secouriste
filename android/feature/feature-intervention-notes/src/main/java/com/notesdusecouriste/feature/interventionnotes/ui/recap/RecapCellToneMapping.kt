package com.notesdusecouriste.feature.interventionnotes.ui.recap



import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedGreenContainer

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedGreenOnContainer

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedYellowContainer

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedYellowOnContainer

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedOrangeContainer

import com.notesdusecouriste.core.ui.theme.ChoiceSelectedOrangeOnContainer

import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceTone



internal data class RecapToneColors(

    val container: Color,

    val onContainer: Color,

)



internal fun ChoiceTone.toRecapCellTone(): RecapCellTone =

    when (this) {

        ChoiceTone.Green -> RecapCellTone.Green

        ChoiceTone.Yellow -> RecapCellTone.Yellow

        ChoiceTone.Orange -> RecapCellTone.Orange

    }



@Composable

internal fun RecapCellTone.toColors(): RecapToneColors? =

    when (this) {

        RecapCellTone.None -> null

        RecapCellTone.Green -> RecapToneColors(

            container = ChoiceSelectedGreenContainer,

            onContainer = ChoiceSelectedGreenOnContainer,

        )

        RecapCellTone.Yellow -> RecapToneColors(

            container = ChoiceSelectedYellowContainer,

            onContainer = ChoiceSelectedYellowOnContainer,

        )

        RecapCellTone.Orange -> RecapToneColors(

            container = ChoiceSelectedOrangeContainer,

            onContainer = ChoiceSelectedOrangeOnContainer,

        )

    }


