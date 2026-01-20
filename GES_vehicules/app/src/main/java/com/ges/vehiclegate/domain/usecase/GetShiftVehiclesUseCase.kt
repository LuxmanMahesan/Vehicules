package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetShiftVehiclesUseCase(
    private val repo: VehicleRepository
) {
    operator fun invoke(shiftStartMillis: Long, shiftEndMillis: Long): Flow<List<VehicleEntry>> =
        repo.observeShiftVehicles(shiftStartMillis, shiftEndMillis)
}
