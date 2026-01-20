package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.repository.VehicleRepository

class UpdateVehicleEntryUseCase(private val repo: VehicleRepository) {
    suspend operator fun invoke(entry: VehicleEntry) = repo.update(entry)
}