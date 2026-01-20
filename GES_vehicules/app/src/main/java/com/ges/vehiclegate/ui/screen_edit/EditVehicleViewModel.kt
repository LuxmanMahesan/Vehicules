package com.ges.vehiclegate.ui.screen_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.Destination
import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.usecase.GetVehicleByIdUseCase
import com.ges.vehiclegate.domain.usecase.UpdateVehicleEntryUseCase
import com.ges.vehiclegate.ui.screen_add.AddVehicleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditVehicleViewModel(
    private val vehicleId: Long,
    private val getVehicleById: GetVehicleByIdUseCase,
    private val updateVehicleEntry: UpdateVehicleEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState(isSaving = true))
    val uiState: StateFlow<AddVehicleUiState> = _uiState

    private var original: VehicleEntry? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val entry = getVehicleById(vehicleId)
                if (entry == null) {
                    _uiState.update { it.copy(isSaving = false, error = "Véhicule introuvable") }
                    return@launch
                }

                original = entry

                _uiState.update {
                    it.copy(
                        plate = entry.plate,
                        companyName = entry.companyName,
                        driverName = entry.driverName,
                        destination = entry.destination,
                        siteContact = entry.siteContact,
                        driverPhone = entry.driverPhone.orEmpty(),
                        notes = entry.notes.orEmpty(),
                        entryGate = entry.entryGate,
                        exitGate = entry.exitGate,
                        photoPath = entry.photoPath,
                        isSaving = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erreur chargement véhicule"
                    )
                }
            }
        }
    }

    fun onPlateChange(v: String) = _uiState.update { it.copy(plate = v, error = null) }
    fun onCompanyChange(v: String) = _uiState.update { it.copy(companyName = v, error = null) }
    fun onDriverNameChange(v: String) = _uiState.update { it.copy(driverName = v, error = null) }
    fun onDestinationChange(v: Destination) = _uiState.update { it.copy(destination = v, error = null) }
    fun onSiteContactChange(v: String) = _uiState.update { it.copy(siteContact = v, error = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(driverPhone = v) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }
    fun onEntryGateChange(v: Gate) = _uiState.update { it.copy(entryGate = v, error = null) }
    fun onExitGateChange(v: Gate) = _uiState.update { it.copy(exitGate = v, error = null) }

    fun save(onSuccess: () -> Unit) {
        val s = _uiState.value
        val base = original

        if (base == null) {
            _uiState.update { it.copy(error = "Impossible d'éditer : données non chargées") }
            return
        }

        val plate = s.plate.trim()
        val company = s.companyName.trim()
        val driverName = s.driverName.trim()
        val siteContact = s.siteContact.trim()

        if (plate.isBlank()) {
            _uiState.update { it.copy(error = "Plaque obligatoire") }
            return
        }
        if (company.isBlank()) {
            _uiState.update { it.copy(error = "Nom de société obligatoire") }
            return
        }
        if (driverName.isBlank()) {
            _uiState.update { it.copy(error = "Nom et prénom du chauffeur obligatoire") }
            return
        }
        if (siteContact.isBlank()) {
            _uiState.update { it.copy(error = "Contact sur site obligatoire") }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val updated = base.copy(
                    plate = plate,
                    companyName = company,
                    driverName = driverName,
                    destination = s.destination,
                    siteContact = siteContact,
                    driverPhone = s.driverPhone.trim().ifBlank { null },
                    notes = s.notes.trim().ifBlank { null },
                    entryGate = s.entryGate,
                    exitGate = s.exitGate
                )

                updateVehicleEntry(updated)

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erreur modification"
                    )
                }
            }
        }
    }
}
