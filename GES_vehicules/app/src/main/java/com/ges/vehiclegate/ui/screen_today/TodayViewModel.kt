package com.ges.vehiclegate.ui.screen_today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.ShiftCalculator
import com.ges.vehiclegate.domain.usecase.GetShiftVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TodayViewModel(
    private val getShiftVehicles: GetShiftVehiclesUseCase,
    private val restoreVehicleOnSite: RestoreVehicleOnSiteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState

    init {
        val now = LocalDateTime.now()
        val shiftNumber = ShiftCalculator.getCurrentShiftNumber(now)
        val reportDate = ShiftCalculator.getReportDateForShift(shiftNumber, now)
        val shiftStartMillis = ShiftCalculator.getShiftStartMillis(shiftNumber, reportDate)
        val shiftEndMillis = ShiftCalculator.getShiftEndMillis(shiftNumber, reportDate)

        viewModelScope.launch {
            getShiftVehicles(shiftStartMillis, shiftEndMillis).collect { list ->
                _uiState.update {
                    it.copy(
                        vehicles = list,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun restore(id: Long) {
        viewModelScope.launch {
            try {
                restoreVehicleOnSite(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erreur rétablir") }
            }
        }
    }
}
