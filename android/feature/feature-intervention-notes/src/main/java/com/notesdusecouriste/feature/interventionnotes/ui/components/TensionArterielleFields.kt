package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.feature.interventionnotes.R

@Composable
fun TensionArterielleFields(
    tensionSys: String,
    tensionDia: String,
    onTensionSysChange: (String) -> Unit,
    onTensionDiaChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabelText(
            label = stringResource(R.string.field_tension_arterielle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BoldLabelField(
                label = stringResource(R.string.field_tension_sys),
                value = tensionSys,
                onValueChange = onTensionSysChange,
                suffix = "mmHg",
                maxDigits = 3,
                modifier = Modifier.weight(1f),
            )
            BoldLabelField(
                label = stringResource(R.string.field_tension_dia),
                value = tensionDia,
                onValueChange = onTensionDiaChange,
                suffix = "mmHg",
                maxDigits = 3,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
