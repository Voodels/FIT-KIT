"use client";

import React, { useEffect, useState } from "react";
import { ProgressAnalytics } from "@/components/progress-analytics";
import { workoutApi } from "@/lib/api";
import { Loader2, Trophy } from "lucide-react";
import { useUserStore } from "@/store/user-store";

export default function ProgressPage() {
  const [workouts, setWorkouts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const userId = useUserStore((state) => state.userId);

  useEffect(() => {
    const fetchWorkouts = async () => {
      if (!userId) {
        setLoading(false);
        return;
      }

      try {
        const data = await workoutApi.getWorkouts(userId);
        
        if (data && data.content && data.content.length > 0) {
          setWorkouts(data.content);
        } else {
          // Fallback to sample data for visualization if nothing exists
          setWorkouts([
            {
              id: "1",
              musclesTargeted: ["quadriceps", "gluteal", "hamstring", "calves"],
              loggedAt: new Date().toISOString(),
            },
            {
              id: "2",
              musclesTargeted: ["chest", "triceps", "deltoids"],
              loggedAt: new Date(Date.now() - 86400000).toISOString(),
            },
            {
              id: "3",
              musclesTargeted: ["upper-back", "lats", "biceps"],
              loggedAt: new Date(Date.now() - 172800000).toISOString(),
            },
            {
              id: "4",
              musclesTargeted: ["chest", "abs"],
              loggedAt: new Date(Date.now() - 259200000).toISOString(),
            }
          ]);
        }
      } catch (error) {
        console.error("Failed to fetch workouts", error);
      } finally {
        setLoading(false);
      }
    };

    fetchWorkouts();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="w-10 h-10 animate-spin text-primary mb-4" />
        <p className="text-muted-foreground font-medium">Analyzing your progress...</p>
      </div>
    );
  }

  return (
    <div className="container max-w-6xl mx-auto py-12 px-6">
      <div className="flex flex-col gap-2 mb-10">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-primary/10 rounded-lg">
            <Trophy className="w-6 h-6 text-primary" />
          </div>
          <h1 className="text-4xl font-black tracking-tight uppercase italic">Your Progress</h1>
        </div>
        <p className="text-muted-foreground text-lg">
          Detailed insights into your training frequency and muscle focus.
        </p>
      </div>

      <ProgressAnalytics workouts={workouts} />
    </div>
  );
}
