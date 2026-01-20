package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.repository.VehicleRepository

class MarkVehicleExitUseCase(
    private val repo: VehicleRepository
) {
    suspend operator fun invoke(id: Long, exitAt: Long) = repo.markExit(id, exitAt)

    suspend fun withGate(id: Long, exitAt: Long, exitGate: Gate) =
        repo.markExitWithGate(id, exitAt, exitGate.label)
}
