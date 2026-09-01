"use client";

import React from 'react';
import { Dna, ShieldCheck, AlertTriangle, Cpu, CheckCircle } from 'lucide-react';

interface GenomicDlCardProps {
  genomic: {
    grnaSequence: string;
    pamSite: string;
    gcContent: number;
    offTargetCleavageScore: number;
    safetyTier: string;
    flaggedLociCount: number;
  };
}

export const GenomicDlCard: React.FC<GenomicDlCardProps> = ({ genomic }) => {
  const isSafe = genomic.offTargetCleavageScore < 0.20;
  const safetyPercentage = Math.round((1 - genomic.offTargetCleavageScore) * 100);

  return (
    <div className="clay-card p-6 border-t-4 border-clay-pink flex flex-col justify-between h-full">
      <div>
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-2xl bg-pink-100 flex items-center justify-center text-clay-pink shadow-clay-sm">
              <Dna className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-pink-600">Module 3</span>
              <h3 className="text-base font-extrabold text-charcoal">Genomic DL & BioKt</h3>
            </div>
          </div>
          <span className="px-2.5 py-1 rounded-full text-[10px] font-bold bg-pink-50 text-pink-700 border border-pink-200">
            BioKt Sequence Parser
          </span>
        </div>

        {/* Safety Metric Card */}
        <div className={`p-4 mb-4 rounded-2xl border ${
          isSafe
            ? 'bg-gradient-to-br from-pink-50 to-rose-100/70 border-pink-200'
            : 'bg-gradient-to-br from-amber-50 to-orange-100 border-amber-300'
        }`}>
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-bold text-pink-900 flex items-center gap-1">
              {isSafe ? <ShieldCheck className="w-3.5 h-3.5 text-pink-600" /> : <AlertTriangle className="w-3.5 h-3.5 text-amber-600" />}
              CRISPR Cleavage Specificity
            </span>
            <span className="text-xs font-mono font-bold text-pink-800 bg-white/70 px-2 py-0.5 rounded-md">
              Safety: {safetyPercentage}%
            </span>
          </div>
          <div className="text-2xl font-black text-charcoal">{safetyPercentage}%</div>
          <p className="text-[11px] text-pink-800/80 font-semibold mt-0.5">{genomic.safetyTier}</p>
        </div>

        {/* gRNA & Sequence Detail */}
        <div className="p-3 rounded-2xl bg-slate-900 text-white font-mono mb-4">
          <div className="text-[10px] text-slate-400 font-bold mb-1 flex items-center justify-between">
            <span>TARGET gRNA (20nt)</span>
            <span className="text-cyan-300">PAM: {genomic.pamSite}</span>
          </div>
          <div className="text-xs font-bold tracking-wider text-emerald-400 break-all">
            {genomic.grnaSequence}
          </div>
        </div>

        {/* Metrics Grid */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="p-3 rounded-2xl bg-white/70 border border-pink-200/50">
            <div className="text-[11px] font-bold text-charcoal/60">BioKt GC Content</div>
            <div className="text-base font-black text-charcoal">{genomic.gcContent}%</div>
            <div className="text-[10px] text-charcoal/50">Optimum (40-60%)</div>
          </div>

          <div className="p-3 rounded-2xl bg-white/70 border border-pink-200/50">
            <div className="text-[11px] font-bold text-charcoal/60">Flagged Off-Targets</div>
            <div className="text-base font-black text-rose-600 flex items-center gap-1">
              {genomic.flaggedLociCount} Sites
            </div>
            <div className="text-[10px] text-charcoal/50">CNN Cleavage Risk</div>
          </div>
        </div>
      </div>

      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-charcoal/60 font-medium">
        <span>NCBI Entrez FASTA Parsed</span>
        <span className="text-pink-600 font-bold flex items-center">
          Loci Map <CheckCircle className="w-3.5 h-3.5 ml-1" />
        </span>
      </div>
    </div>
  );
};
