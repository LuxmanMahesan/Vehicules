package com.ges.vehiclegate.ui.screen_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.Destination
import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.usecase.AddVehicleEntryUseCase
import com.ges.vehiclegate.util.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val addVehicleEntry: AddVehicleEntryUseCase,
    private val dateTimeProvider: DateTimeProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState

    fun onPlateChange(v: String) = _uiState.update { it.copy(plate = v, error = null) }
    fun onCompanyChange(v: String) = _uiState.update { it.copy(companyName = v, error = null) }
    fun onDriverNameChange(v: String) = _uiState.update { it.copy(driverName = v, error = null) }
    fun onDestinationChange(v: Destination) = _uiState.update { it.copy(destination = v, error = null) }
    fun onSiteContactChange(v: String) = _uiState.update { it.copy(siteContact = v, error = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(driverPhone = v) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }
    fun onEntryGateChange(v: Gate) = _uiState.update { it.copy(entryGate = v, error = null) }
    fun onExitGateChange(v: Gate) = _uiState.update { it.copy(exitGate = v, error = null) }

    fun onPhotoCaptured(path: String) {
        _uiState.update { it.copy(photoPath = path, error = null) }
    }

    fun save(onSuccess: () -> Unit) {
        val s = _uiState.value

        val plate = s.plate.trim()
        val company = s.companyName.trim()
        val driverName = s.driverName.trim()
        val siteContact = s.siteContact.trim()

        if (s.photoPath == null) {
            _uiState.update { it.copy(error = "Photo du bon de livraison obligatoire") }
            return
        }
        if (plate.isBlank()) {
            _uiState.update { it.copy(error = "Plaque d'immatriculation obligatoire") }
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
                val now = dateTimeProvider.nowMillis()
                val entry = VehicleEntry(
                    plate = plate,
                    companyName = company,
                    driverName = driverName,
                    destination = s.destination,
                    siteContact = siteContact,
                    driverPhone = s.driverPhone.trim().ifBlank { null },
                    notes = s.notes.trim().ifBlank { null },
                    entryGate = s.entryGate,
                    exitGate = s.exitGate,
                    arrivalAt = now,
                    exitAt = null,
                    photoPath = s.photoPath
                )
                addVehicleEntry(entry)
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Erreur inconnue") }
            }
        }
    }
}
