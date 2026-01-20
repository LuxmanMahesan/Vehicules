package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.repository.VehicleRepository

class RestoreVehicleOnSiteUseCase(
    private val repo: VehicleRepository
) {
    suspend operator fun invoke(id: Long) = repo.restoreOnSite(id)
}
