package com.example.biomedix.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "pipeline_reports")
data class CachedReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diseaseName: String,
    val hubGene: String,
    val druggabilityScore: Float,
    val crisprSafetyScore: Float,
    val verdict: String,
    val pdbId: String,
    val grnaSequence: String,
    val pocketVolume: Float,
    val offTargetCount: Int,
    val centralityMethod: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface BioMedixDao {
    @Query("SELECT * FROM pipeline_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<CachedReportEntity>>

    @Query("SELECT * FROM pipeline_reports WHERE diseaseName = :disease LIMIT 1")
    suspend fun getReportForDisease(disease: String): CachedReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CachedReportEntity): Long

    @Query("DELETE FROM pipeline_reports WHERE id = :id")
    suspend fun deleteReport(id: Long)

    @Query("DELETE FROM pipeline_reports")
    suspend fun clearAll()
}

@Database(entities = [CachedReportEntity::class], version = 1, exportSchema = false)
abstract class BioMedixDatabase : RoomDatabase() {
    abstract fun dao(): BioMedixDao

    companion object {
        @Volatile
        private var INSTANCE: BioMedixDatabase? = null

        fun getDatabase(context: Context): BioMedixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BioMedixDatabase::class.java,
                    "biomedix_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
