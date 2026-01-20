package com.ges.vehiclegate.data.repository

import com.ges.vehiclegate.data.local.dao.VehicleEntryDao
import com.ges.vehiclegate.data.mapper.toDomain
import com.ges.vehiclegate.data.mapper.toEntity
import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val dao: VehicleEntryDao
) : VehicleRepository {

    override fun observeOnSiteVehicles(): Flow<List<VehicleEntry>> =
        dao.observeOnSiteVehicles().map { list -> list.map { it.toDomain() } }

    override fun observeShiftVehicles(): Flow<List<VehicleEntry>> =
        dao.observeShiftVehicles().map { list -> list.map { it.toDomain() } }

    override suspend fun add(entry: VehicleEntry): Long =
        dao.insert(entry.toEntity())

    override suspend fun markExit(id: Long, exitAt: Long) {
        dao.markExit(id, exitAt)
    }

    override suspend fun markExitWithGate(id: Long, exitAt: Long, exitGateLabel: String) {
        dao.markExitWithGate(id, exitAt, exitGateLabel)
    }

    override suspend fun getById(id: Long): VehicleEntry? =
        dao.getById(id)?.toDomain()

    override suspend fun update(entry: VehicleEntry) {
        dao.update(entry.toEntity())
    }

    override suspend fun restoreOnSite(id: Long) {
        dao.restoreOnSite(id)
    }

    override suspend fun archiverJournee() {
        dao.archiverJournee()
    }

    override suspend fun supprimerVehiculesSortis() {
        dao.supprimerVehiculesSortis()
    }

    override suspend fun getAllVehiclesForReport(): List<VehicleEntry> =
        dao.getAllVehiclesForReport().map { it.toDomain() }
}
