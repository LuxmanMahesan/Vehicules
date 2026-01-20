package com.ges.vehiclegate.data.mapper

import com.ges.vehiclegate.data.local.entity.VehicleEntryEntity
import com.ges.vehiclegate.domain.model.Destination
import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.model.VehicleEntry

fun VehicleEntryEntity.toDomain(): VehicleEntry =
    VehicleEntry(
        id = id,
        plate = plate,
        companyName = companyName,
        driverName = driverName,
        destination = Destination.fromLabel(destinationLabel),
        siteContact = siteContact,
        driverPhone = driverPhone,
        notes = notes,
        entryGate = Gate.fromLabel(entryGateLabel),
        exitGate = Gate.fromLabel(exitGateLabel),
        arrivalAt = arrivalAt,
        exitAt = exitAt,
        photoPath = photoPath,
        archive = archive,
        onSite = onSite
    )

fun VehicleEntry.toEntity(): VehicleEntryEntity =
    VehicleEntryEntity(
        id = id,
        plate = plate,
        companyName = companyName,
        driverName = driverName,
        destinationLabel = destination.label,
        siteContact = siteContact,
        driverPhone = driverPhone,
        notes = notes,
        entryGateLabel = entryGate.label,
        exitGateLabel = exitGate.label,
        arrivalAt = arrivalAt,
        exitAt = exitAt,
        photoPath = photoPath,
        archive = archive,
        onSite = onSite
    )
