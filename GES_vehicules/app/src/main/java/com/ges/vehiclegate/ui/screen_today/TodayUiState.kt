package com.ges.vehiclegate.ui.screen_today

import com.ges.vehiclegate.domain.model.VehicleEntry

data class TodayUiState(
    val vehicles: List<VehicleEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
