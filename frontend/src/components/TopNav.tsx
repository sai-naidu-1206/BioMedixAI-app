"use client";

import React from 'react';
import { Activity, Dna, ShieldCheck, Sparkles, Layers } from 'lucide-react';

interface TopNavProps {
  activeTab: string;
  onSelectTab: (tab: string) => void;
}

export const TopNav: React.FC<TopNavProps> = ({ activeTab, onSelectTab }) => {
  const navItems = [
    { id: 'all', label: 'All Modules', icon: Layers },
    { id: 'network', label: 'Network Biology', icon: Activity },
    { id: 'structural', label: 'Structural ML', icon: Sparkles },
    { id: 'genomic', label: 'Genomic DL & BioKt', icon: Dna },
    { id: 'synthesis', label: 'Clinical Strategy', icon: ShieldCheck },
  ];

  return (
    <header className="w-full max-w-6xl mx-auto pt-6 px-4 mb-8">
      <div className="clay-card p-4 flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Branding */}
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-tr from-clay-violet via-clay-periwinkle to-clay-mint flex items-center justify-center shadow-clay-glow-violet">
            <Dna className="w-6 h-6 text-white animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-extrabold tracking-tight text-charcoal">BioMedixAI</h1>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 border border-emerald-300">
                v2.4 Core
              </span>
            </div>
            <p className="text-xs text-charcoal/60 font-medium">3D Multi-Modal Genomic & Structural Intelligence</p>
          </div>
        </div>

        {/* Filter / Category Pills */}
        <nav className="flex flex-wrap items-center gap-2">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onSelectTab(item.id)}
                className={`flex items-center gap-1.5 px-3.5 py-2 rounded-full text-xs font-semibold transition-all duration-200 ${
                  isActive
                    ? 'bg-clay-violet text-white shadow-clay-glow-violet scale-105'
                    : 'bg-white/60 text-charcoal/70 hover:bg-white hover:text-charcoal hover:scale-102'
                }`}
              >
                <Icon className="w-3.5 h-3.5" />
                {item.label}
              </button>
            );
          })}
        </nav>
      </div>
    </header>
  );
};
