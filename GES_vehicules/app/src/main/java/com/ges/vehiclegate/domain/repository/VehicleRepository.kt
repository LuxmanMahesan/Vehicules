package com.ges.vehiclegate.domain.repository

import com.ges.vehiclegate.domain.model.VehicleEntry
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    // Accueil: véhicules sur site
    fun observeOnSiteVehicles(): Flow<List<VehicleEntry>>

    // Véhicules du shift: tous les véhicules non archivés
    fun observeShiftVehicles(): Flow<List<VehicleEntry>>

    suspend fun add(entry: VehicleEntry): Long
    suspend fun markExit(id: Long, exitAt: Long)
    suspend fun markExitWithGate(id: Long, exitAt: Long, exitGateLabel: String)
    suspend fun getById(id: Long): VehicleEntry?
    suspend fun update(entry: VehicleEntry)
    suspend fun restoreOnSite(id: Long)
    suspend fun archiverJournee()

    // Supprimer les véhicules sortis après envoi du PDF
    suspend fun supprimerVehiculesSortis()

    // Récupérer tous les véhicules pour le PDF
    suspend fun getAllVehiclesForReport(): List<VehicleEntry>
}
