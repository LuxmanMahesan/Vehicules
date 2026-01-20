package com.ges.vehiclegate.ui.screen_home

import com.ges.vehiclegate.domain.model.VehicleEntry

data class HomeUiState(
    val vehiclesOnSite: List<VehicleEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
