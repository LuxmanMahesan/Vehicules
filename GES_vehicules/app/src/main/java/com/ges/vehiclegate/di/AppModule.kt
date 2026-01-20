package com.ges.vehiclegate.di

import android.content.Context
import androidx.room.Room
import com.ges.vehiclegate.data.local.db.AppDatabase
import com.ges.vehiclegate.data.repository.VehicleRepositoryImpl
import com.ges.vehiclegate.domain.repository.VehicleRepository
import com.ges.vehiclegate.util.DateTimeProvider
import com.ges.vehiclegate.util.SystemDateTimeProvider

object AppModule {

    @Volatile private var db: AppDatabase? = null
    @Volatile private var repo: VehicleRepository? = null
    @Volatile private var dateTimeProvider: DateTimeProvider? = null

    fun provideDatabase(context: Context): AppDatabase =
        db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "vehiclegate.db"
            ).fallbackToDestructiveMigration().build().also { db = it }
        }

    fun provideVehicleRepository(context: Context): VehicleRepository =
        repo ?: synchronized(this) {
            repo ?: VehicleRepositoryImpl(
                dao = provideDatabase(context).vehicleEntryDao()
            ).also { repo = it }
        }

    fun provideDateTimeProvider(): DateTimeProvider =
        dateTimeProvider ?: synchronized(this) {
            dateTimeProvider ?: SystemDateTimeProvider().also { dateTimeProvider = it }
        }
}
