"use client";

import React from 'react';
import { Play, Sparkles, Database, Code, RefreshCw } from 'lucide-react';

interface FloatingActionBarProps {
  onQuickRun: () => void;
  isLoading: boolean;
}

export const FloatingActionBar: React.FC<FloatingActionBarProps> = ({ onQuickRun, isLoading }) => {
  return (
    <div className="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-50">
      <div className="clay-card px-5 py-2.5 flex items-center gap-3 backdrop-blur-md bg-white/90 border border-white/80 shadow-clay-lg rounded-full">
        <div className="flex items-center gap-2 text-xs font-bold text-charcoal/70 pr-2 border-r border-slate-200">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse" />
          <span>BioKt + Spring Core Active</span>
        </div>

        <button
          onClick={onQuickRun}
          disabled={isLoading}
          className="clay-button-primary px-5 py-2 flex items-center gap-2 text-xs font-bold shadow-clay-glow-violet disabled:opacity-50"
        >
          {isLoading ? (
            <>
              <RefreshCw className="w-3.5 h-3.5 animate-spin" />
              <span>Analyzing...</span>
            </>
          ) : (
            <>
              <Sparkles className="w-3.5 h-3.5 text-amber-300" />
              <span>Re-Run Active Target</span>
            </>
          )}
        </button>
      </div>
    </div>
  );
};
