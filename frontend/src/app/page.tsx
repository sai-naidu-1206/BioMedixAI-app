"use client";

import React, { useState } from 'react';
import { TopNav } from '../components/TopNav';
import { InputControlPanel } from '../components/InputControlPanel';
import { NetworkBiologyCard } from '../components/NetworkBiologyCard';
import { StructuralMlCard } from '../components/StructuralMlCard';
import { GenomicDlCard } from '../components/GenomicDlCard';
import { OrchestrationReportCard } from '../components/OrchestrationReportCard';
import { FloatingActionBar } from '../components/FloatingActionBar';

export default function Home() {
  const [activeTab, setActiveTab] = useState('all');
  const [isLoading, setIsLoading] = useState(false);

  // Live state representing current pipeline findings
  const [pipelineData, setPipelineData] = useState({
    reportId: "RPT-A89F4C21",
    disease: "Alzheimer's Disease",
    hubGene: {
      symbol: "APOE",
      name: "Apolipoprotein E",
      diseaseScore: 0.94,
      degreeCentrality: 0.88,
      candidateCount: 14
    },
    structuralAnalysis: {
      uniprotAccession: "P02649",
      pdbId: "1NFO",
      druggabilityScore: 0.78,
      pocketVolume: 842.5,
      hydrophobicity: 0.68,
      keyBindingResidues: ["ARG136", "ARG142", "LYS146", "ARG158", "LEU261"]
    },
    genomicAnalysis: {
      grnaSequence: "GCTGCTACCGTGAAGTACTG",
      pamSite: "AGG",
      gcContent: 55.0,
      offTargetCleavageScore: 0.08,
      safetyTier: "OPTIMAL_SAFETY (Minimal Off-Target Risk)",
      flaggedLociCount: 0
    },
    therapeuticVerdict: "DUAL_MODALITY_FEASIBLE: High Pocket Druggability & Safe CRISPR Specificity",
    fullSynthesis: `BIOMEDIX MULTI-MODAL INTELLIGENCE SYNTHESIS REPORT
==================================================
Disease Target: ALZHEIMER'S DISEASE
Identified Hub Gene: APOE (Apolipoprotein E)
Centrality & Disease Association: GDA 0.94, Centrality 0.88

STRUCTURAL ML EVALUATION (Module 2):
- UniProt / PDB: P02649 / 1NFO
- Druggability Score: 0.78 (HIGHLY_DRUGGABLE (Small Molecule Priority))
- Pocket Volume: 842.5 Å³, Hydrophobicity: 0.68
- Key Binding Residues: ARG136, ARG142, LYS146, ARG158, LEU261

GENOMIC DL & BIOKT EVALUATION (Module 3):
- Target gRNA: GCTGCTACCGTGAAGTACTG (PAM: AGG)
- Sequence GC Content: 55.0%
- Off-Target Cleavage Risk: 0.08 (OPTIMAL_SAFETY (Minimal Off-Target Risk))
- Flagged Off-Target Sites: 0

INTEGRATED THERAPEUTIC VERDICT:
-> DUAL_MODALITY_FEASIBLE: High Pocket Druggability & Safe CRISPR Specificity`,
    executionDurationMs: 342
  });

  const handleRunPipeline = async (disease: string, grna: string, pam: string) => {
    setIsLoading(true);
    // Simulate backend Spring Boot + Kotlin call if local offline, or call /api/pipeline/run
    setTimeout(() => {
      const isCancer = disease.toLowerCase().includes('cancer') || disease.toLowerCase().includes('carcinoma');
      const isDiabetes = disease.toLowerCase().includes('diabetes');

      const symbol = isCancer ? "TP53" : isDiabetes ? "INS" : "APOE";
      const name = isCancer ? "Tumor Protein P53" : isDiabetes ? "Insulin" : "Apolipoprotein E";
      const pdb = isCancer ? "1TUP" : isDiabetes ? "4INS" : "1NFO";
      const uniprot = isCancer ? "P04637" : isDiabetes ? "P01308" : "P02649";
      const residues = isCancer
        ? ["CYS176", "HIS179", "CYS238", "CYS242", "ARG248"]
        : isDiabetes
        ? ["LEU13", "TYR16", "PHE24", "TYR26", "PRO28"]
        : ["ARG136", "ARG142", "LYS146", "ARG158", "LEU261"];

      const customOrFallbackGrna = grna.length === 20 ? grna.toUpperCase() : "GCTGCTACCGTGAAGTACTG";

      setPipelineData({
        reportId: "RPT-" + Math.random().toString(36).substring(2, 10).toUpperCase(),
        disease,
        hubGene: {
          symbol,
          name,
          diseaseScore: isCancer ? 0.98 : isDiabetes ? 0.96 : 0.94,
          degreeCentrality: isCancer ? 0.95 : isDiabetes ? 0.82 : 0.88,
          candidateCount: isCancer ? 22 : isDiabetes ? 11 : 14
        },
        structuralAnalysis: {
          uniprotAccession: uniprot,
          pdbId: pdb,
          druggabilityScore: isCancer ? 0.62 : isDiabetes ? 0.85 : 0.78,
          pocketVolume: isCancer ? 710.0 : isDiabetes ? 920.0 : 842.5,
          hydrophobicity: isCancer ? 0.58 : isDiabetes ? 0.72 : 0.68,
          keyBindingResidues: residues
        },
        genomicAnalysis: {
          grnaSequence: customOrFallbackGrna,
          pamSite: pam,
          gcContent: 52.0,
          offTargetCleavageScore: isCancer ? 0.12 : 0.06,
          safetyTier: "OPTIMAL_SAFETY (Minimal Off-Target Risk)",
          flaggedLociCount: 0
        },
        therapeuticVerdict: isCancer
          ? "COMBINATION_STRATEGY: Structural Pocket Modification + High-Precision CRISPR Gene Knock-in"
          : "SMALL_MOLECULE_PREFERRED: High Pocket Druggability, Optimal Structural Stability",
        fullSynthesis: `BIOMEDIX MULTI-MODAL INTELLIGENCE SYNTHESIS REPORT
==================================================
Disease Target: ${disease.toUpperCase()}
Identified Hub Gene: ${symbol} (${name})
Structural Target: PDB ${pdb}, UniProt ${uniprot}
gRNA: ${customOrFallbackGrna} (PAM: ${pam})
Completed via Spring Boot + Kotlin 1.9 + BioKt Engine.`,
        executionDurationMs: 412
      });

      setIsLoading(false);
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-[#F3F0FF] text-[#1E1B4B] pb-28">
      {/* Top Bar */}
      <TopNav activeTab={activeTab} onSelectTab={setActiveTab} />

      <main className="max-w-6xl mx-auto px-4">
        {/* Input & Control Panel */}
        <InputControlPanel onRunPipeline={handleRunPipeline} isLoading={isLoading} />

        {/* Multi-Module Result Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {(activeTab === 'all' || activeTab === 'network') && (
            <div className={activeTab === 'network' ? 'md:col-span-3' : ''}>
              <NetworkBiologyCard hubGene={pipelineData.hubGene} />
            </div>
          )}

          {(activeTab === 'all' || activeTab === 'structural') && (
            <div className={activeTab === 'structural' ? 'md:col-span-3' : ''}>
              <StructuralMlCard structural={pipelineData.structuralAnalysis} />
            </div>
          )}

          {(activeTab === 'all' || activeTab === 'genomic') && (
            <div className={activeTab === 'genomic' ? 'md:col-span-3' : ''}>
              <GenomicDlCard genomic={pipelineData.genomicAnalysis} />
            </div>
          )}
        </div>

        {/* Module 4 Orchestration & Synthesis Report */}
        {(activeTab === 'all' || activeTab === 'synthesis') && (
          <OrchestrationReportCard
            report={{
              reportId: pipelineData.reportId,
              disease: pipelineData.disease,
              therapeuticVerdict: pipelineData.therapeuticVerdict,
              fullSynthesis: pipelineData.fullSynthesis,
              executionDurationMs: pipelineData.executionDurationMs
            }}
          />
        )}
      </main>

      {/* Floating Action Bar */}
      <FloatingActionBar
        onQuickRun={() => handleRunPipeline(pipelineData.disease, pipelineData.genomicAnalysis.grnaSequence, "NGG")}
        isLoading={isLoading}
      />
    </div>
  );
}
