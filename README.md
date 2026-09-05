# 🧬 BioMedixAI: Multi-Modal Biomedical Intelligence Platform

<div align="center">

![BioMedixAI Banner](https://img.shields.io/badge/BioMedixAI-v2.4.0-8B5CF6?style=for-the-badge&logo=dna&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android Compose](https://img.shields.io/badge/Android-Jetpack_Compose-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-14-black?style=for-the-badge&logo=next.js&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-JSONB-316192?style=for-the-badge&logo=postgresql&logoColor=white)

<p align="center">
  <strong>Autonomous Multi-Agent Target Discovery, Structural Druggability ML, and CRISPR Off-Target Prediction Pipeline.</strong>
</p>

[Key Features](#-key-features) • [System Architecture](#-system-architecture) • [Module Breakdown](#-multi-agent-pipeline-modules) • [Tech Stack](#-tech-stack) • [Quick Start](#-quick-start-guide) • [API Reference](#-api-endpoints)

---

</div>

## 🌟 Overview

**BioMedixAI** is an end-to-end computational biology and precision medicine platform. By combining network pharmacology, structural machine learning, and genomic deep learning via **BioKt**, BioMedixAI automates drug target identification and evaluates dual-modality therapeutic feasibility (small molecules vs. CRISPR gene editing).

The platform provides two presentation interfaces:
1. **Android Application**: Native Jetpack Compose mobile app featuring tactile 3D interactive mesh controls, local Room database caching, and real-time PPI interactome visualization.
2. **Web Client (`frontend/`)**: Modern **3D Claymorphic** and Neumorphic dashboard crafted in Next.js and Tailwind CSS with pastel depth gradients.
3. **Backend Service (`backend/`)**: Spring Boot 3 + Kotlin 1.9 REST service orchestrating multi-database queries, BioKt sequence analytics, and PostgreSQL `JSONB` persistence.

---

## 🎨 Aesthetic & UI Directives (3D Claymorphic Design)

- **Palette**: Clean off-white canvas with soft lavender undertones (`#F3F0FF`), pastel violet/periwinkle (`#8B5CF6`, `#A78BFA`), electric mint green (`#34D399` to `#06B6D4`), warm amber (`#F59E0B`), and hot pink (`#EC4899`).
- **Typography & Readability**: Dark charcoal (`#1E1B4B`) body text optimized for contrast and clinical clarity.
- **Surface Elevation**: Claymorphic rounded components (`rounded-3xl`), dual-diffuse drop shadows, and specular rim lighting.

---

## 🔬 Multi-Agent Pipeline Modules

```
Disease Indication / gRNA Input
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│  Module 1: Network Biology & PPI Centrality                │
│  - DisGeNET Gene-Disease Association (GDA) query           │
│  - STRING DB Interactome Network generation                │
│  - Degree & Eigenvector Centrality scoring ➔ Hub Gene      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Isolated Hub Gene (e.g. APOE, TP53)
              ┌────────────────┴────────────────┐
              ▼                                 ▼
┌──────────────────────────────┐ ┌──────────────────────────────┐
│ Module 2: Structural ML      │ │ Module 3: Genomic DL & BioKt │
│ - UniProt & RCSB PDB Mapping │ │ - NCBI Entrez FASTA Parser   │
│ - DeepPocket Cavity Sizing   │ │ - BioKt CRISPR PAM Scanning  │
│ - Druggability Index Scoring │ │ - CNN Off-Target Cleavage ML │
└──────────────┬───────────────┘ └──────────────┬───────────────┘
               │                                │
               └────────────────┬───────────────┘
                                │ Concurrent Results
                                ▼
┌─────────────────────────────────────────────────────────────┐
│  Module 4: Orchestration & Consensus Verdict                │
│  - Multi-Modal Synthesis Engine                             │
│  - PostgreSQL JSONB Storage (Pocket vectors + Loci logs)    │
│  - Actionable Clinical Strategy Report                      │
└─────────────────────────────────────────────────────────────┘
```

### Module 1: Network Biology
- DisGeNET API client with built-in `MockGeneProvider` fallback for offline reliability.
- STRING DB interaction graph parsing.
- Centrality metric calculation to isolate the primary **Hub Gene**.

### Module 2: Structural Machine Learning
- UniProt accession and RCSB PDB structural resolution mapping.
- Binding pocket feature extraction (volume in Å³, Kyte-Doolittle hydrophobicity, cavity depth, surface area).
- Druggability index assessment for small molecule feasibility.

### Module 3: Genomic Deep Learning & BioKt
- Core bioinformatics engine powered by **`BioKt`**:
  - FASTA record parsing, streaming, and validation.
  - GC content computation and reverse complement generation.
  - SpCas9 PAM motif scanning (`NGG`, `NAG`, `NGA`).
- Convolutional neural surrogate evaluating seed-region mismatches and off-target cleavage risk.

### Module 4: Orchestration & Clinical Consensus
- Asynchronous orchestration using Kotlin Coroutines (`async`/`await`).
- Generates therapeutic consensus:
  - `DUAL_MODALITY_FEASIBLE`: High pocket druggability & safe CRISPR specificity.
  - `SMALL_MOLECULE_PREFERRED`: High pocket druggability with elevated off-target flags.
  - `GENE_EDITING_PREFERRED`: Low druggability pocket, highly specific gRNA cleavage.
  - `COMBINATION_BIOLOGIC_INVESTIGATION`: Complex target requiring multi-target strategy.
- Saves reports to PostgreSQL with `JSONB` indices for downstream analytics.

---

## 🏗️ Project Structure

```bash
biomedix-ai/
├── app/                                 # Android Native Application
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt              # Main entry point (Compose UI)
│   │   ├── biomedix/
│   │   │   ├── biokt/BioKt.kt           # Embedded BioKt Engine
│   │   │   ├── module1_network/         # PPI Graph & Centrality
│   │   │   ├── module2_structural/      # 3D PDB Pocket Analyzer
│   │   │   ├── module3_genomic/         # CRISPR Off-target Engine
│   │   │   ├── module4_orchestration/   # Synthesis & Consensus
│   │   │   ├── data/local/              # Room Database (Cached Reports)
│   │   │   └── ui/                      # Jetpack Compose Screens & 3D Canvas
│   │   └── ui/theme/                    # Claymorphic Color System & Theme
│   └── build.gradle.kts
│
├── backend/                             # Spring Boot 3 + Kotlin REST Backend
│   ├── src/main/kotlin/com/biomedix/
│   │   ├── BioMedixApplication.kt       # Application Bootstrap
│   │   ├── biokt/BioKt.kt               # BioKt FASTA & PAM Engine
│   │   ├── model/Entities.kt            # JPA Entities & JSONB Support
│   │   ├── repository/Repositories.kt   # Spring Data Repositories
│   │   ├── module1_network/             # DisGeNET & STRING DB Service
│   │   ├── module2_structural/          # PDB & Druggability ML Service
│   │   ├── module3_genomic/             # NCBI FASTA & Genomic DL Service
│   │   ├── module4_orchestration/       # Coroutine Pipeline Orchestrator
│   │   └── controller/                  # REST Controllers & Endpoints
│   ├── src/main/resources/
│   │   ├── application.yml              # DB & External API Configuration
│   │   └── schema.sql                   # PostgreSQL DDL with JSONB
│   └── build.gradle.kts
│
├── frontend/                            # Next.js 14 Claymorphic Dashboard
│   ├── src/
│   │   ├── app/page.tsx                 # Main Analytics Dashboard
│   │   ├── components/
│   │   │   ├── TopNav.tsx               # Minimalist Header & Filters
│   │   │   ├── InputControlPanel.tsx    # Disease & gRNA Input Card
│   │   │   ├── NetworkBiologyCard.tsx   # PPI Interactome & Hub Gene
│   │   │   ├── StructuralMlCard.tsx     # Pocket Gauge & PDB Residues
│   │   │   ├── GenomicDlCard.tsx        # BioKt Safety Dial & Loci
│   │   │   ├── OrchestrationReportCard.tsx # Synthesis Report Terminal
│   │   │   └── FloatingActionBar.tsx    # Curved Action Button
│   │   └── styles/claymorphism.css      # Claymorphic CSS Utility Classes
│   ├── tailwind.config.js               # Custom Shadows & 3D Radii
│   └── package.json
│
├── metadata.json                        # Platform Configuration
├── build.gradle.kts                     # Root Gradle Configuration
└── README.md
```

---

## 💻 Tech Stack

| Domain | Technology / Library | Description |
| :--- | :--- | :--- |
| **Backend Framework** | Spring Boot 3.2.3, Kotlin 1.9.22 | High-concurrency reactive REST microservice |
| **Bioinformatics Engine** | BioKt | FASTA sequence processing, reverse complement, PAM scanning |
| **Database** | PostgreSQL 16 + Hypersistence JSONB | Storing structured records with semi-structured pocket & loci vectors |
| **Concurrency** | Kotlin Coroutines (`kotlinx-coroutines`) | Non-blocking parallel execution of structural and genomic models |
| **Web Client** | Next.js 14, Tailwind CSS, Lucide Icons | 3D Claymorphic interface with Neumorphic depth |
| **Mobile Client** | Jetpack Compose, Material 3, Room | Android app with local offline persistence and custom 3D canvas |

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 17+** (Eclipse Temurin or OpenJDK)
- **Node.js 18+** & npm / pnpm
- **PostgreSQL 14+** (optional; mock fallbacks enabled by default)
- **Android Studio Hedgehog+** (for Android client development)

---

### 1. Running the Spring Boot Backend

```bash
cd backend

# Configure environment variables (optional)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/biomedix
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

# Run Gradle build & start application
./gradlew bootRun
```
The REST API will start on **`http://localhost:8080`**.

---

### 2. Running the Next.js Claymorphic Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```
Open **`http://localhost:3000`** in your browser.

---

### 3. Running the Android Application

Open the root repository directory in Android Studio:
```bash
# Build debug APK via Gradle
./gradlew :app:assembleDebug

# Run Robolectric unit tests
./gradlew :app:testDebugUnitTest
```

---

## 📡 API Endpoints

### 1. Launch Full Multi-Agent Pipeline
- **Method**: `POST /api/pipeline/run`
- **Request Body**:
```json
{
  "disease": "Alzheimer's Disease",
  "customGrna": "GCTGCTACCGTGAAGTACTG",
  "pamMotif": "NGG",
  "prioritizeSmallMolecule": true
}
```
- **Response**:
```json
{
  "reportId": "RPT-A89F4C21",
  "status": "COMPLETED",
  "disease": "Alzheimer's Disease",
  "hubGene": {
    "symbol": "APOE",
    "name": "Apolipoprotein E",
    "diseaseScore": 0.94,
    "degreeCentrality": 0.88,
    "candidateCount": 14
  },
  "structuralAnalysis": {
    "uniprotAccession": "P02649",
    "pdbId": "1NFO",
    "druggabilityScore": 0.78,
    "pocketVolume": 842.5,
    "hydrophobicity": 0.68,
    "keyBindingResidues": ["ARG136", "ARG142", "LYS146", "ARG158", "LEU261"]
  },
  "genomicAnalysis": {
    "grnaSequence": "GCTGCTACCGTGAAGTACTG",
    "pamSite": "AGG",
    "gcContent": 55.0,
    "offTargetCleavageScore": 0.08,
    "safetyTier": "OPTIMAL_SAFETY (Minimal Off-Target Risk)",
    "flaggedLociCount": 0
  },
  "therapeuticVerdict": "DUAL_MODALITY_FEASIBLE: High Pocket Druggability & Safe CRISPR Specificity",
  "fullSynthesis": "...",
  "executionDurationMs": 342
}
```

### 2. BioKt FASTA Parser
- **Method**: `POST /api/biokt/fasta/parse`
- **Payload**: Raw FASTA text
- **Response**: Array of parsed `FastaRecord` objects with calculated GC content and nucleotide frequencies.

### 3. CRISPR PAM Scanner
- **Method**: `POST /api/biokt/crispr/scan?pam=NGG`
- **Payload**: Raw nucleotide sequence
- **Response**: List of candidate gRNA target sites with PAM motif alignments.

### 4. Fetch Stored Reports
- **Method**: `GET /api/reports`
- **Response**: List of historical reports stored in PostgreSQL with JSONB data.

---

## 🗄️ Database Schema

The PostgreSQL schema utilizes `JSONB` columns for semi-structured molecular feature sets:

```sql
CREATE TABLE therapeutic_reports (
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

CREATE INDEX idx_therapeutic_reports_disease ON therapeutic_reports(disease_name);
CREATE INDEX idx_therapeutic_reports_hub_gene ON therapeutic_reports(hub_gene_symbol);
CREATE INDEX idx_therapeutic_reports_pocket_features ON therapeutic_reports USING gin(pocket_features);
CREATE INDEX idx_therapeutic_reports_off_target_sites ON therapeutic_reports USING gin(off_target_sites);
```

---

## 🤝 Contributing

1. Fork the Project repository
2. Create your Feature Branch (`git checkout -b feature/BioKtEnhancement`)
3. Commit your changes (`git commit -m 'Add BioKt multi-sequence alignment'`)
4. Push to the branch (`git push origin feature/BioKtEnhancement`)
5. Open a Pull Request

---

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for more information.

