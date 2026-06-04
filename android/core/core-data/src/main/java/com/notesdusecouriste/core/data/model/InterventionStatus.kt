package com.notesdusecouriste.core.data.model

enum class InterventionStatus(val storageValue: String) {
    DRAFT("draft"),
    CLOSED("closed"),
    ;

    companion object {
        fun fromStorage(value: String): InterventionStatus =
            entries.firstOrNull { it.storageValue == value } ?: DRAFT
    }
}
