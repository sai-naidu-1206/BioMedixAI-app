"use client";

import React from 'react';
import { Sparkles, Box, Droplets, Layers, ExternalLink } from 'lucide-react';

interface StructuralMlCardProps {
  structural: {
    uniprotAccession: string;
    pdbId: string;
    druggabilityScore: number;
    pocketVolume: number;
    hydrophobicity: number;
    keyBindingResidues: string[];
  };
}

export const StructuralMlCard: React.FC<StructuralMlCardProps> = ({ structural }) => {
  const percentage = Math.round(structural.druggabilityScore * 100);

  return (
    <div className="clay-card p-6 border-t-4 border-clay-mint flex flex-col justify-between h-full">
      <div>
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-2xl bg-emerald-100 flex items-center justify-center text-clay-mint shadow-clay-sm">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-emerald-600">Module 2</span>
              <h3 className="text-base font-extrabold text-charcoal">Structural ML & Druggability</h3>
            </div>
          </div>
          <span className="px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
            RCSB PDB & DeepPocket
          </span>
        </div>

        {/* Druggability Score Dial Block */}
        <div className="clay-card-mint p-4 mb-4 rounded-2xl flex items-center justify-between">
          <div>
            <div className="text-xs font-bold text-emerald-800">Druggability Index</div>
            <div className="text-2xl font-black text-emerald-950">{percentage}%</div>
            <p className="text-[11px] text-emerald-700 font-semibold mt-0.5">
              {percentage >= 70 ? 'High Pocket Suitability' : 'Moderate Pocket Feasibility'}
            </p>
          </div>

          {/* Circular Score Gauge */}
          <div className="relative w-16 h-16 flex items-center justify-center">
            <svg className="w-full h-full transform -rotate-90" viewBox="0 0 36 36">
              <path
                className="text-emerald-200"
                strokeWidth="3.8"
                stroke="currentColor"
                fill="none"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <path
                className="text-emerald-500 transition-all duration-1000 ease-out"
                strokeDasharray={`${percentage}, 100`}
                strokeWidth="3.8"
                strokeLinecap="round"
                stroke="currentColor"
                fill="none"
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
              />
            </svg>
            <span className="absolute text-xs font-black text-emerald-900">{percentage}%</span>
          </div>
        </div>

        {/* Structural Metrics */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="p-3 rounded-2xl bg-white/70 border border-clay-mint/30">
            <div className="text-[11px] font-bold text-charcoal/60 flex items-center gap-1">
              <Box className="w-3 h-3 text-clay-mint" />
              Pocket Volume
            </div>
            <div className="text-base font-black text-charcoal">{structural.pocketVolume} Å³</div>
            <div className="text-[10px] text-charcoal/50">Binding cavity size</div>
          </div>

          <div className="p-3 rounded-2xl bg-white/70 border border-clay-mint/30">
            <div className="text-[11px] font-bold text-charcoal/60 flex items-center gap-1">
              <Droplets className="w-3 h-3 text-cyan-500" />
              Hydrophobicity
            </div>
            <div className="text-base font-black text-charcoal">{structural.hydrophobicity.toFixed(2)}</div>
            <div className="text-[10px] text-charcoal/50">Kyte-Doolittle index</div>
          </div>
        </div>

        {/* Residues Pill Breakdown */}
        <div className="p-3 rounded-2xl bg-white/60 border border-slate-200">
          <div className="text-[11px] font-bold text-charcoal/70 mb-2 flex items-center justify-between">
            <span>Key Binding Pocket Residues</span>
            <span className="text-[10px] font-mono text-clay-mint font-bold">PDB: {structural.pdbId}</span>
          </div>
          <div className="flex flex-wrap gap-1.5">
            {structural.keyBindingResidues.map((res, idx) => (
              <span
                key={idx}
                className="text-[10px] font-mono font-bold px-2 py-0.5 rounded-lg bg-emerald-100/70 text-emerald-800 border border-emerald-300"
              >
                {res}
              </span>
            ))}
          </div>
        </div>
      </div>

      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-charcoal/60 font-medium">
        <span>UniProt: <strong className="text-charcoal">{structural.uniprotAccession}</strong></span>
        <span className="text-clay-mint font-bold flex items-center">
          PDB 3D Mesh <ExternalLink className="w-3.5 h-3.5 ml-1" />
        </span>
      </div>
    </div>
  );
};
