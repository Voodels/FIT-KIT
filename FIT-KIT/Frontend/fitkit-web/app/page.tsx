"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { workoutApi } from "@/lib/api";
import { WorkoutCard } from "@/components/workout-card";
import { Button } from "@/components/ui/button";
import { Plus, Dumbbell, History, Search, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { useUserStore } from "@/store/user-store";
import { useRouter } from "next/navigation";

export default function Home() {
  const [workouts, setWorkouts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const userId = useUserStore((state) => state.userId);
  const clearUser = useUserStore((state) => state.clearUser);
  const router = useRouter();

  useEffect(() => {
    const fetchWorkouts = async () => {
      if (!userId) {
        router.replace("/auth");
        return;
      }
      const data = await workoutApi.getWorkouts();
      if (data?.unauthorized) {
        clearUser();
        router.replace("/auth");
        return;
      }
      
      if (data && data.content && data.content.length > 0) {
        setWorkouts(data.content);
      } else {
        // Mock data only if user exists but has no logs yet
        setWorkouts([
          {
            id: "1",
            photoUrl: "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80&w=600",
            caption: "Crushed heavy leg day today! New PR on squats.",
            musclesTargeted: ["quadriceps", "gluteal", "hamstring"],
            loggedAt: new Date().toISOString(),
          },
          {
            id: "2",
            photoUrl: "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?auto=format&fit=crop&q=80&w=600",
            caption: "Chest pump was unreal. Focused on slow eccentrics.",
            musclesTargeted: ["chest", "triceps", "deltoids"],
            loggedAt: new Date(Date.now() - 86400000).toISOString(),
          }
        ]);
      }
      setLoading(false);
    };

    fetchWorkouts();
  }, [userId, router]);

  return (
    <div className="flex flex-col min-h-screen">
      <header className="sticky top-0 z-10 bg-white/80 dark:bg-zinc-900/80 backdrop-blur-md border-b px-6 py-4 flex items-center justify-between">
        <h2 className="text-xl font-black tracking-tight uppercase italic hidden md:block">Journal</h2>
        <div className="relative w-full max-w-md mx-auto md:mx-0">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input 
            placeholder="Search journals..." 
            className="pl-10 bg-muted/50 border-none rounded-full h-10 w-full focus-visible:ring-primary/20"
          />
        </div>
      </header>

      <div className="max-w-2xl mx-auto w-full py-12 px-6">
        <div className="flex items-center justify-between mb-10">
          <div>
            <h1 className="text-3xl font-black tracking-tight uppercase italic">Recent Logs</h1>
            <p className="text-muted-foreground text-sm font-medium">Your fitness journey, documented.</p>
          </div>
          <div className="flex gap-2">
            <Button variant="secondary" size="sm" className="rounded-full font-bold text-[10px] uppercase tracking-wider">Recent</Button>
            <Button variant="ghost" size="sm" className="rounded-full font-bold text-[10px] uppercase tracking-wider">By Muscle</Button>
          </div>
        </div>

        {loading ? (
          <div className="space-y-12">
            {[1, 2].map((i) => (
              <div key={i} className="space-y-4">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-muted animate-pulse" />
                  <div className="w-32 h-4 bg-muted animate-pulse rounded" />
                </div>
                <div className="aspect-square w-full bg-muted animate-pulse rounded-3xl" />
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-12 pb-24">
            {workouts.length > 0 ? (
              workouts.map((workout) => (
                <WorkoutCard key={workout.id} workout={workout} />
              ))
            ) : (
              <div className="text-center py-24 bg-white dark:bg-zinc-900 rounded-[32px] border shadow-sm">
                <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-6">
                  <Dumbbell className="w-10 h-10 text-primary opacity-40" />
                </div>
                <h3 className="text-xl font-black tracking-tight uppercase italic mb-2">No entries yet</h3>
                <p className="text-muted-foreground mb-8 max-w-[240px] mx-auto text-sm font-medium">
                  Your journal is empty. Time to hit the gym and log your first session!
                </p>
                <Link href="/workouts/new">
                  <Button className="rounded-xl h-12 px-8 font-bold shadow-lg shadow-primary/20">
                    Log Your First Workout
                  </Button>
                </Link>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
