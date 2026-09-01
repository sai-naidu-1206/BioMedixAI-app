-- BioMedix AI PostgreSQL Schema with JSONB Column Support
CREATE TABLE IF NOT EXISTS therapeutic_reports (
    id VARCHAR(64) PRIMARY KEY,
    disease_name VARCHAR(255) NOT NULL,
    hub_gene_symbol VARCHAR(64) NOT NULL,
    hub_gene_name VARCHAR(255) NOT NULL,
    uniprot_accession VARCHAR(64) NOT NULL,
    pdb_id VARCHAR(32) NOT NULL,
    grna_sequence VARCHAR(128) NOT NULL,
    pam_site VARCHAR(16) NOT NULL,
    druggability_score DOUBLE PRECISION NOT NULL,
    crispr_off_target_score DOUBLE PRECISION NOT NULL,
    clinical_verdict VARCHAR(64) NOT NULL,
    synthesis_report TEXT NOT NULL,
    pocket_features JSONB NOT NULL,
    off_target_sites JSONB NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_therapeutic_reports_disease ON therapeutic_reports(disease_name);
CREATE INDEX IF NOT EXISTS idx_therapeutic_reports_hub_gene ON therapeutic_reports(hub_gene_symbol);
CREATE INDEX IF NOT EXISTS idx_therapeutic_reports_pocket_features ON therapeutic_reports USING gin(pocket_features);
CREATE INDEX IF NOT EXISTS idx_therapeutic_reports_off_target_sites ON therapeutic_reports USING gin(off_target_sites);
