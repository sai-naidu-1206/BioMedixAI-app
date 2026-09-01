"use client";

import React from 'react';
import { Activity, Hub, Network, Star, ArrowUpRight } from 'lucide-react';

interface NetworkBiologyCardProps {
  hubGene: {
    symbol: string;
    name: string;
    diseaseScore: number;
    degreeCentrality: number;
    candidateCount: number;
  };
}

export const NetworkBiologyCard: React.FC<NetworkBiologyCardProps> = ({ hubGene }) => {
  return (
    <div className="clay-card p-6 border-t-4 border-clay-violet flex flex-col justify-between h-full">
      <div>
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-2xl bg-violet-100 flex items-center justify-center text-clay-violet shadow-clay-sm">
              <Activity className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-clay-violet">Module 1</span>
              <h3 className="text-base font-extrabold text-charcoal">Network Biology & PPI</h3>
            </div>
          </div>
          <span className="px-2.5 py-1 rounded-full text-[10px] font-bold bg-violet-50 text-clay-violet border border-violet-200">
            STRING DB Centrality
          </span>
        </div>

        {/* Hub Gene Highlight Block */}
        <div className="clay-card-violet p-4 mb-4 rounded-2xl">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-bold text-clay-violet flex items-center gap-1">
              <Star className="w-3.5 h-3.5 fill-clay-violet" />
              Isolated Primary Hub Gene
            </span>
            <span className="text-xs font-mono font-bold text-clay-violet bg-white/70 px-2 py-0.5 rounded-md">
              Score: {(hubGene.diseaseScore * 100).toFixed(0)}%
            </span>
          </div>
          <div className="text-2xl font-black text-charcoal">{hubGene.symbol}</div>
          <p className="text-xs text-charcoal/70 font-medium">{hubGene.name}</p>
        </div>

        {/* Interactive Metrics Grid */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="p-3 rounded-2xl bg-white/70 border border-clay-lavender/30">
            <div className="text-[11px] font-bold text-charcoal/60">Degree Centrality</div>
            <div className="text-lg font-black text-clay-violet">
              {(hubGene.degreeCentrality * 100).toFixed(1)}%
            </div>
            <div className="w-full bg-violet-100 h-1.5 rounded-full mt-1.5 overflow-hidden">
              <div
                className="bg-clay-violet h-full rounded-full transition-all duration-500"
                style={{ width: `${hubGene.degreeCentrality * 100}%` }}
              />
            </div>
          </div>

          <div className="p-3 rounded-2xl bg-white/70 border border-clay-lavender/30">
            <div className="text-[11px] font-bold text-charcoal/60">Candidate Interactome</div>
            <div className="text-lg font-black text-charcoal">{hubGene.candidateCount} Genes</div>
            <div className="text-[10px] text-charcoal/50 font-medium mt-1">DisGeNET Ranked</div>
          </div>
        </div>

        {/* Dynamic PPI Graph Canvas Simulation */}
        <div className="p-3 rounded-2xl bg-slate-900 text-white relative overflow-hidden h-32 flex items-center justify-center">
          <div className="absolute inset-0 opacity-25 bg-[radial-gradient(#8B5CF6_1px,transparent_1px)] [background-size:12px_12px]" />
          
          {/* Central Hub Node */}
          <div className="relative z-10 flex flex-col items-center">
            <div className="w-12 h-12 rounded-full bg-gradient-to-r from-violet-500 to-indigo-600 flex items-center justify-center text-xs font-black shadow-lg shadow-violet-500/50 animate-pulse border-2 border-white/80">
              {hubGene.symbol}
            </div>
            <span className="text-[9px] font-mono text-cyan-300 mt-1 font-bold">HUB NODE (C_deg={hubGene.degreeCentrality.toFixed(2)})</span>
          </div>

          {/* Surrounding Nodes */}
          <div className="absolute top-3 left-4 w-7 h-7 rounded-full bg-slate-700 border border-slate-500 flex items-center justify-center text-[8px] font-bold">
            APP
          </div>
          <div className="absolute bottom-3 left-8 w-7 h-7 rounded-full bg-slate-700 border border-slate-500 flex items-center justify-center text-[8px] font-bold">
            PSEN1
          </div>
          <div className="absolute top-4 right-6 w-7 h-7 rounded-full bg-slate-700 border border-slate-500 flex items-center justify-center text-[8px] font-bold">
            TREM2
          </div>
          <div className="absolute bottom-4 right-8 w-7 h-7 rounded-full bg-slate-700 border border-slate-500 flex items-center justify-center text-[8px] font-bold">
            MAPT
          </div>
        </div>
      </div>

      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-charcoal/60 font-medium">
        <span>GDA Centrality Score Computed</span>
        <span className="text-clay-violet font-bold flex items-center">
          Details <ArrowUpRight className="w-3.5 h-3.5 ml-0.5" />
        </span>
      </div>
    </div>
  );
};
