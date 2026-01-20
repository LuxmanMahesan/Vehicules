package com.ges.vehiclegate.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ges.vehiclegate.data.local.dao.VehicleEntryDao
import com.ges.vehiclegate.data.local.entity.VehicleEntryEntity

@Database(
    entities = [VehicleEntryEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleEntryDao(): VehicleEntryDao
}

