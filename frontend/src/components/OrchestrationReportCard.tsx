"use client";

import React, { useState } from 'react';
import { ShieldCheck, FileText, Database, CheckCircle2, ChevronDown, ChevronUp, Copy, Check } from 'lucide-react';

interface OrchestrationReportCardProps {
  report: {
    reportId: string;
    disease: string;
    therapeuticVerdict: string;
    fullSynthesis: string;
    executionDurationMs: number;
  };
}

export const OrchestrationReportCard: React.FC<OrchestrationReportCardProps> = ({ report }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isCopied, setIsCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(report.fullSynthesis);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2000);
  };

  return (
    <div className="clay-card p-6 md:p-8 mt-8 border-2 border-clay-violet/40 shadow-clay-lg">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-clay-violet to-clay-periwinkle flex items-center justify-center text-white shadow-clay-glow-violet">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-extrabold uppercase tracking-wider text-clay-violet">Module 4: Orchestration</span>
              <span className="text-[10px] font-mono font-bold px-2 py-0.5 rounded-full bg-slate-200 text-slate-700">
                {report.reportId}
              </span>
            </div>
            <h3 className="text-xl font-extrabold text-charcoal">Integrated Multi-Modal Clinical Strategy Report</h3>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 text-xs font-semibold px-3 py-1.5 rounded-full bg-emerald-100 text-emerald-800 border border-emerald-300">
            <Database className="w-3.5 h-3.5" />
            PostgreSQL JSONB Persisted ({report.executionDurationMs}ms)
          </div>

          <button
            onClick={handleCopy}
            className="p-2 rounded-xl bg-white text-charcoal/70 hover:text-clay-violet hover:bg-violet-50 transition-all border border-clay-lavender/40"
            title="Copy Synthesis Text"
          >
            {isCopied ? <Check className="w-4 h-4 text-emerald-600" /> : <Copy className="w-4 h-4" />}
          </button>
        </div>
      </div>

      {/* Primary Clinical Verdict Callout */}
      <div className="p-5 rounded-2xl bg-gradient-to-r from-violet-600 via-indigo-600 to-purple-700 text-white shadow-clay-glow-violet mb-6">
        <div className="text-xs font-bold text-violet-200 uppercase tracking-wider mb-1 flex items-center gap-1.5">
          <CheckCircle2 className="w-4 h-4 text-emerald-300" />
          Multi-Agent Consensus Therapeutic Verdict
        </div>
        <div className="text-lg md:text-xl font-extrabold tracking-tight">
          {report.therapeuticVerdict}
        </div>
      </div>

      {/* Collapsible Synthesis Terminal Viewer */}
      <div className="rounded-2xl bg-slate-950 text-slate-200 p-4 border border-slate-800">
        <div className="flex items-center justify-between pb-3 border-b border-slate-800 mb-3">
          <div className="flex items-center gap-2 text-xs font-mono text-cyan-400">
            <FileText className="w-4 h-4" />
            <span>BioMedix Multi-Modal Synthesis Engine v2.4</span>
          </div>
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="text-xs text-slate-400 hover:text-white flex items-center gap-1 font-semibold"
          >
            {isExpanded ? 'Collapse Report' : 'Expand Full Text'}
            {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
          </button>
        </div>

        <pre
          className={`font-mono text-xs text-slate-300 whitespace-pre-wrap leading-relaxed transition-all duration-300 overflow-hidden ${
            isExpanded ? 'max-h-[800px] overflow-y-auto' : 'max-h-36'
          }`}
        >
          {report.fullSynthesis}
        </pre>
      </div>
    </div>
  );
};
