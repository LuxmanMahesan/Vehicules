package com.ges.vehiclegate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ges.vehiclegate.data.local.entity.VehicleEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleEntryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: VehicleEntryEntity): Long

    @Update
    suspend fun update(entry: VehicleEntryEntity)

    // Accueil: véhicules sur site (onSite = true)
    // Triés par ordre d'arrivée: le plus ancien en haut, le plus récent en bas
    @Query("""
        SELECT * FROM vehicle_entries
        WHERE onSite = 1 AND archive = 0
        ORDER BY arrivalAt ASC
    """)
    fun observeOnSiteVehicles(): Flow<List<VehicleEntryEntity>>

    // Véhicules du shift: TOUS les véhicules non archivés
    // Triés par ordre d'arrivée: le plus ancien en haut, le plus récent en bas
    @Query("""
        SELECT * FROM vehicle_entries
        WHERE archive = 0
        ORDER BY arrivalAt ASC
    """)
    fun observeShiftVehicles(): Flow<List<VehicleEntryEntity>>

    @Query("SELECT * FROM vehicle_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VehicleEntryEntity?

    // Marquer la sortie: met onSite = false et enregistre l'heure de sortie
    @Query("UPDATE vehicle_entries SET onSite = 0, exitAt = :exitAt WHERE id = :id AND archive = 0")
    suspend fun markExit(id: Long, exitAt: Long)

    @Query("UPDATE vehicle_entries SET onSite = 0, exitAt = :exitAt, exitGateLabel = :exitGateLabel WHERE id = :id AND archive = 0")
    suspend fun markExitWithGate(id: Long, exitAt: Long, exitGateLabel: String)

    // Restaurer sur site: remet onSite = true et efface l'heure de sortie
    @Query("UPDATE vehicle_entries SET onSite = 1, exitAt = NULL WHERE id = :id AND archive = 0")
    suspend fun restoreOnSite(id: Long)

    // Archiver tous les véhicules (fin de journée complète)
    @Query("UPDATE vehicle_entries SET archive = 1 WHERE archive = 0")
    suspend fun archiverJournee()

    // Supprimer les véhicules sortis (onSite = false) après envoi du PDF
    @Query("DELETE FROM vehicle_entries WHERE onSite = 0 AND archive = 0")
    suspend fun supprimerVehiculesSortis()

    // Récupérer tous les véhicules pour le PDF (sur site + sortis)
    @Query("""
        SELECT * FROM vehicle_entries
        WHERE archive = 0
        ORDER BY arrivalAt ASC
    """)
    suspend fun getAllVehiclesForReport(): List<VehicleEntryEntity>
}
