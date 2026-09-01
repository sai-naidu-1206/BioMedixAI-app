package com.biomedix.repository

import com.biomedix.model.TherapeuticReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TherapeuticReportRepository : JpaRepository<TherapeuticReportEntity, String> {

    fun findByDiseaseNameIgnoreCase(diseaseName: String): List<TherapeuticReportEntity>

    fun findByHubGeneSymbolIgnoreCase(hubGeneSymbol: String): List<TherapeuticReportEntity>

    @Query(
        value = "SELECT * FROM therapeutic_reports WHERE pocket_features->>'volume' >= :minVolume",
        nativeQuery = true
    )
    fun findByMinPocketVolume(@Param("minVolume") minVolume: String): List<TherapeuticReportEntity>

    @Query(
        value = "SELECT * FROM therapeutic_reports WHERE jsonb_array_length(off_target_sites) > 0 ORDER BY created_at DESC LIMIT 20",
        nativeQuery = true
    )
    fun findRecentReportsWithOffTargets(): List<TherapeuticReportEntity>
}
