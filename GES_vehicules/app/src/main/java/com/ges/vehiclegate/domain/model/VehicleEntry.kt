package com.ges.vehiclegate.domain.model

data class VehicleEntry(
    val id: Long = 0L,
    val plate: String,
    val companyName: String,
    val driverName: String,
    val destination: Destination,
    val siteContact: String,
    val driverPhone: String?,
    val notes: String?,
    val entryGate: Gate = Gate.EUGENIE,
    val exitGate: Gate = Gate.EUGENIE,
    val arrivalAt: Long,
    val exitAt: Long?,
    val photoPath: String?,
    val archive: Boolean = false,
    val onSite: Boolean = true
)
