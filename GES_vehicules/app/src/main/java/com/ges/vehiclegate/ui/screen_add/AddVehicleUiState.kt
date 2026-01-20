package com.ges.vehiclegate.ui.screen_add

import com.ges.vehiclegate.domain.model.Destination
import com.ges.vehiclegate.domain.model.Gate

data class AddVehicleUiState(
    val plate: String = "",
    val companyName: String = "",
    val driverName: String = "",
    val destination: Destination = Destination.AUTRE,
    val siteContact: String = "",
    val driverPhone: String = "",
    val notes: String = "",
    val entryGate: Gate = Gate.EUGENIE,
    val exitGate: Gate = Gate.EUGENIE,
    val photoPath: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)
