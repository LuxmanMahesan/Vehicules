package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.repository.VehicleRepository

class GetVehicleByIdUseCase(private val repo: VehicleRepository) {
    suspend operator fun invoke(id: Long): VehicleEntry? = repo.getById(id)
}