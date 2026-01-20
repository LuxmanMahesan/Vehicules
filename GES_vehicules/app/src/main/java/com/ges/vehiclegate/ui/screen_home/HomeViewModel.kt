package com.ges.vehiclegate.ui.screen_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.usecase.GetOnSiteVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.MarkVehicleExitUseCase
import com.ges.vehiclegate.util.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getOnSiteVehicles: GetOnSiteVehiclesUseCase,
    private val markVehicleExit: MarkVehicleExitUseCase,
    private val dateTimeProvider: DateTimeProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            getOnSiteVehicles().collect { list ->
                _uiState.update { it.copy(vehiclesOnSite = list, isLoading = false, error = null) }
            }
        }
    }

    fun markExit(id: Long, exitGate: Gate) {
        viewModelScope.launch {
            try {
                markVehicleExit.withGate(id, dateTimeProvider.nowMillis(), exitGate)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erreur sortie") }
            }
        }
    }
}
