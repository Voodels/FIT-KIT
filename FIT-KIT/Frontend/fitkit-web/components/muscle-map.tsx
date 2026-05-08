"use client";

import React, { useMemo, useEffect, useState } from "react";
import dynamic from "next/dynamic";

const Body = dynamic(() => import("react-native-body-highlighter"), {
  ssr: false,
});

/**
 * Valid slugs for HichamELBSI/react-native-body-highlighter:
 * "abs", "adductors", "ankles", "biceps", "calves", "chest", "deltoids", "feet", "forearm", 
 * "gluteal", "hamstring", "hands", "hair", "head", "knees", "lower-back", "neck", "obliques", 
 * "quadriceps", "tibialis", "trapezius", "triceps", "upper-back"
 */

interface MuscleMapProps {
  selectedMuscles?: string[];
  onMusclesChange?: (muscles: string[]) => void;
  heatmapData?: Record<string, number>;
  interactive?: boolean;
}

export function MuscleMap({ 
  selectedMuscles = [], 
  onMusclesChange, 
  heatmapData,
  interactive = true 
}: MuscleMapProps) {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);
  
  const bodyData = useMemo(() => {
    if (heatmapData) {
      return Object.entries(heatmapData).map(([slug, intensity]) => ({
        slug: slug as any,
        intensity: intensity,
        color: `rgba(255, 0, 0, ${intensity / 100})`
      }));
    } else {
      return selectedMuscles.map(slug => ({
        slug: slug as any,
        color: "#EF4444" // Bright red for selection
      }));
    }
  }, [selectedMuscles, heatmapData]);

  const handlePress = (part: any) => {
    if (!interactive || !onMusclesChange) return;
    
    const slug = part.slug;
    const isSelected = selectedMuscles.includes(slug);
    const newSelection = isSelected 
      ? selectedMuscles.filter(m => m !== slug)
      : [...selectedMuscles, slug];
    
    onMusclesChange(newSelection);
  };

  return (
    <div className="flex flex-col gap-6 items-center p-6 bg-card rounded-2xl border shadow-sm">
      {!mounted ? (
        <div className="w-64 h-64 flex items-center justify-center bg-muted/20 rounded-xl animate-pulse">
          <span className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">Loading Anatomy...</span>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-12 w-full max-w-sm">
          <div className="flex flex-col items-center">
            <span className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground mb-4">Front</span>
            <div className="w-32 h-64">
              <Body
                data={bodyData as any}
                onBodyPartPress={handlePress}
                side="front"
                gender="male"
                scale={0.8}
              />
            </div>
          </div>

          <div className="flex flex-col items-center">
            <span className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground mb-4">Back</span>
            <div className="w-32 h-64">
              <Body
                data={bodyData as any}
                onBodyPartPress={handlePress}
                side="back"
                gender="male"
                scale={0.8}
              />
            </div>
          </div>
        </div>
      )}

      {!heatmapData && interactive && (
        <div className="flex flex-wrap gap-2 justify-center mt-2">
          {selectedMuscles.length > 0 ? (
            selectedMuscles.map(m => (
              <span key={m} className="px-3 py-1 bg-primary text-primary-foreground text-[10px] font-bold rounded-full uppercase tracking-tight">
                {m.replace('-', ' ')}
              </span>
            ))
          ) : (
            <p className="text-sm text-muted-foreground animate-pulse">Tap muscles to select</p>
          )}
        </div>
      )}
    </div>
  );
}
