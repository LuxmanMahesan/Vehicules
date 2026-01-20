package com.ges.vehiclegate.domain.model

enum class Gate(val label: String) {
    EUGENIE("Eugénie"),
    GANDHI("Gandhi"),
    SAINT_JAMES("Saint James");

    companion object {
        fun fromLabel(label: String): Gate =
            entries.firstOrNull { it.label == label } ?: EUGENIE
    }
}
