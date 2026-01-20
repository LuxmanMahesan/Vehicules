package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.repository.VehicleRepository

class FinishDayUseCase(
    private val repo: VehicleRepository
) {
    suspend operator fun invoke() {
        repo.archiverJournee()
    }
}
