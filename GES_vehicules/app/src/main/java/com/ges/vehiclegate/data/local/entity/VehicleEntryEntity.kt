package com.ges.vehiclegate.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_entries",
    indices = [
        Index(value = ["arrivalAt"]),
        Index(value = ["exitAt"]),
        Index(value = ["plate"])
    ]
)
data class VehicleEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val plate: String,
    val companyName: String,
    val driverName: String,
    val destinationLabel: String,
    val siteContact: String,
    val driverPhone: String?,
    val notes: String?,
    val entryGateLabel: String,
    val exitGateLabel: String,
    val arrivalAt: Long,
    val exitAt: Long?,
    val photoPath: String?,
    val archive: Boolean = false,
    val onSite: Boolean = true
)
