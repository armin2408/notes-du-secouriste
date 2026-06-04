package com.notesdusecouriste.feature.interventionnotes.ui.recap



data class RecapLine(

    val label: String,

    val value: String,

)

data class RecapSubSection(

    val title: String,

    val lines: List<RecapLine>,

)



enum class RecapCellTone {

    None,

    Green,

    Yellow,

    Orange,

}



data class RecapTableCell(

    val text: String,

    val tone: RecapCellTone = RecapCellTone.None,

)



sealed class RecapTableRow {

    data class SectionHeader(val title: String) : RecapTableRow()

    data class Measure(

        val label: String,

        val cells: List<RecapTableCell>,

    ) : RecapTableRow()

}



data class RecapMeasuresTable(

    val columnHeaders: List<String>,

    val rows: List<RecapTableRow>,

)



data class InterventionRecap(

    val identityLines: List<RecapLine> = emptyList(),

    val measuresTable: RecapMeasuresTable? = null,

    val questionnaires: List<RecapSubSection> = emptyList(),

    val comment: String? = null,

) {

    val isEmpty: Boolean =

        identityLines.isEmpty() &&
            measuresTable == null &&
            questionnaires.isEmpty() &&
            comment.isNullOrBlank()

}


