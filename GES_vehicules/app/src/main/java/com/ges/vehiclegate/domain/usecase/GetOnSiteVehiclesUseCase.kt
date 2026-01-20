package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetOnSiteVehiclesUseCase(
    private val repo: VehicleRepository
) {
    operator fun invoke(): Flow<List<VehicleEntry>> = repo.observeOnSiteVehicles()
}
