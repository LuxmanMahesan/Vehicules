package com.ges.vehiclegate.domain.model

enum class Destination(val label: String) {
    ENTREPOT("Entrepôt"),
    ATELIER("Atelier"),
    BUREAUX("Bureaux"),
    BOREAL("Boréal"),
    RESTAURATION("Restauration"),
    AUTRE("Autre");

    companion object {
        fun fromLabel(label: String): Destination =
            entries.firstOrNull { it.label == label } ?: AUTRE
    }
}
