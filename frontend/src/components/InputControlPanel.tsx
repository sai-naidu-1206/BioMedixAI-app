"use client";

import React, { useState } from 'react';
import { Play, Sparkles, Wand2, Search, SlidersHorizontal, RefreshCw } from 'lucide-react';

interface InputControlPanelProps {
  onRunPipeline: (disease: string, grna: string, pam: string) => void;
  isLoading: boolean;
}

export const InputControlPanel: React.FC<InputControlPanelProps> = ({ onRunPipeline, isLoading }) => {
  const [disease, setDisease] = useState("Alzheimer's Disease");
  const [grna, setGrna] = useState("GCTGCTACCGTGAAGTACTG");
  const [pam, setPam] = useState("NGG");

  const sampleDiseases = [
    { label: "Alzheimer's", query: "Alzheimer's Disease", grna: "GCTGCTACCGTGAAGTACTG" },
    { label: "Non-Small Cell Lung Cancer", query: "Lung Carcinoma", grna: "TTCGGCCATGTTGTCGATGT" },
    { label: "Type 2 Diabetes", query: "Type 2 Diabetes Mellitus", grna: "AGCTCGTACCAGTTGACTCG" },
    { label: "Breast Cancer (BRCA1)", query: "Breast Neoplasms", grna: "CAGTAGTGGTAATACCCCCA" }
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (disease.trim()) {
      onRunPipeline(disease, grna, pam);
    }
  };

  return (
    <div className="clay-card p-6 md:p-8 mb-8 border-2 border-clay-lavender/30">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-xl bg-violet-100 text-clay-violet">
            <SlidersHorizontal className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-charcoal">Multi-Agent Target Pipeline Launcher</h2>
            <p className="text-xs text-charcoal/60">Configure disease ontology target & CRISPR guide parameters</p>
          </div>
        </div>

        {/* Preset Quick Badges */}
        <div className="hidden lg:flex items-center gap-2">
          <span className="text-xs text-charcoal/50 font-medium mr-1">Quick Select:</span>
          {sampleDiseases.map((sample, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => {
                setDisease(sample.query);
                setGrna(sample.grna);
              }}
              className="text-xs font-semibold px-3 py-1.5 rounded-full bg-white/80 text-clay-violet border border-clay-lavender/50 hover:bg-violet-50 transition-all"
            >
              {sample.label}
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
          {/* Disease Input */}
          <div className="md:col-span-5 space-y-1.5">
            <label className="text-xs font-bold text-charcoal/80 flex items-center gap-1.5">
              <Search className="w-3.5 h-3.5 text-clay-violet" />
              Target Disease / Indication
            </label>
            <input
              type="text"
              value={disease}
              onChange={(e) => setDisease(e.target.value)}
              placeholder="e.g. Alzheimer's Disease, Glioblastoma"
              className="clay-input w-full px-4 py-3 text-sm font-medium"
              required
            />
          </div>

          {/* gRNA Sequence Input */}
          <div className="md:col-span-4 space-y-1.5">
            <label className="text-xs font-bold text-charcoal/80 flex items-center gap-1.5">
              <Wand2 className="w-3.5 h-3.5 text-clay-mint" />
              Optional 20nt gRNA Sequence (BioKt)
            </label>
            <input
              type="text"
              value={grna}
              onChange={(e) => setGrna(e.target.value.toUpperCase())}
              placeholder="20nt sequence (e.g. GCTGCT...)"
              maxLength={20}
              className="clay-input w-full px-4 py-3 text-sm font-mono tracking-wider font-semibold uppercase"
            />
          </div>

          {/* PAM Motif Selection */}
          <div className="md:col-span-3 space-y-1.5">
            <label className="text-xs font-bold text-charcoal/80 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-clay-amber" />
              PAM Motif Target
            </label>
            <select
              value={pam}
              onChange={(e) => setPam(e.target.value)}
              className="clay-input w-full px-4 py-3 text-sm font-bold bg-white text-charcoal"
            >
              <option value="NGG">SpCas9 (NGG)</option>
              <option value="NAG">SpCas9 Non-Canonical (NAG)</option>
              <option value="NGA">Cas9-VQR Variant (NGA)</option>
            </select>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-between pt-2 gap-3">
          <div className="text-xs text-charcoal/50 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
            BioKt Sequence Parser & PostgreSQL JSONB Database Ready
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="clay-button-primary px-8 py-3 flex items-center gap-2 text-sm shadow-clay-glow-violet disabled:opacity-50"
          >
            {isLoading ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                Executing Pipeline Modules...
              </>
            ) : (
              <>
                <Play className="w-4 h-4 fill-white" />
                Launch Multi-Agent Pipeline
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
