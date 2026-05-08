"use client";

import React, { useMemo } from "react";
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer, 
  Cell,
  AreaChart,
  Area
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { MuscleMap } from "@/components/muscle-map";

interface AnalyticsProps {
  workouts: any[];
}

export function ProgressAnalytics({ workouts }: AnalyticsProps) {
  // 0. Summary Stats
  const totalWorkouts = workouts.length;
  const uniqueDays = useMemo(() => {
    const days = new Set(workouts.map(w => (w.loggedAt || w.createdAt || "").split(/[T ]/)[0]));
    return days.size;
  }, [workouts]);

  // 1. Calculate Muscle Frequency
  const muscleFrequency = useMemo(() => {
    const counts: Record<string, number> = {};
    workouts.forEach(w => {
      if (w.musclesTargeted) {
        w.musclesTargeted.forEach((m: string) => {
          counts[m] = (counts[m] || 0) + 1;
        });
      }
    });
    return Object.entries(counts)
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  }, [workouts]);

  // 2. Heatmap intensity (0-100)
  const heatmapData = useMemo(() => {
    const data: Record<string, number> = {};
    if (muscleFrequency.length === 0) return data;
    const max = Math.max(...muscleFrequency.map(m => m.count));
    muscleFrequency.forEach(m => {
      data[m.name] = (m.count / max) * 100;
    });
    return data;
  }, [muscleFrequency]);

  // 3. Activity over time (last 7 days)
  const activityData = useMemo(() => {
    const last7Days = Array.from({ length: 7 }, (_, i) => {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }).reverse();

    return last7Days.map(date => {
      const count = workouts.filter(w => {
        const workoutDate = (w.loggedAt || w.createdAt || "").split(/[T ]/)[0];
        return workoutDate === date;
      }).length;
      return { 
        date: date.split("-").slice(1).join("/"), 
        count 
      };
    });
  }, [workouts]);

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      {/* Summary Stats Bar */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="border-none shadow-sm bg-primary/5">
          <CardContent className="p-6">
            <p className="text-[10px] font-black uppercase tracking-widest text-primary/60 mb-1">Total Sessions</p>
            <p className="text-3xl font-black italic tracking-tighter">{totalWorkouts}</p>
          </CardContent>
        </Card>
        <Card className="border-none shadow-sm bg-primary/5">
          <CardContent className="p-6">
            <p className="text-[10px] font-black uppercase tracking-widest text-primary/60 mb-1">Active Days</p>
            <p className="text-3xl font-black italic tracking-tighter">{uniqueDays}</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Heatmap Section */}
        <Card className="border-none shadow-md overflow-hidden">
          <CardHeader className="bg-muted/30">
            <CardTitle className="text-xl">Muscle Heatmap</CardTitle>
            <CardDescription>Visual distribution of your training focus</CardDescription>
          </CardHeader>
          <CardContent className="pt-6">
            <MuscleMap heatmapData={heatmapData} interactive={false} />
          </CardContent>
        </Card>

        {/* Frequency Chart */}
        <Card className="border-none shadow-md overflow-hidden">
          <CardHeader className="bg-muted/30">
            <CardTitle className="text-xl">Target Frequency</CardTitle>
            <CardDescription>How often you hit each muscle group</CardDescription>
          </CardHeader>
          <CardContent className="pt-6 h-[400px]">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={muscleFrequency} layout="vertical" margin={{ left: 20 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} opacity={0.3} />
                <XAxis type="number" hide />
                <YAxis 
                  dataKey="name" 
                  type="category" 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{ fontSize: 12, fontWeight: 500 }}
                />
                <Tooltip 
                  cursor={{ fill: 'transparent' }}
                  contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}
                />
                <Bar dataKey="count" radius={[0, 4, 4, 0]}>
                  {muscleFrequency.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={`hsl(var(--primary) / ${Math.max(0.3, 1 - index * 0.1)})`} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* Activity Timeline */}
      <Card className="border-none shadow-md overflow-hidden">
        <CardHeader className="bg-muted/30">
          <CardTitle className="text-xl">Training Consistency</CardTitle>
          <CardDescription>Your workout frequency over the last 7 days</CardDescription>
        </CardHeader>
        <CardContent className="pt-6 h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={activityData}>
              <defs>
                <linearGradient id="colorCount" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="hsl(var(--primary))" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="hsl(var(--primary))" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} opacity={0.2} />
              <XAxis dataKey="date" axisLine={false} tickLine={false} tick={{ fontSize: 12 }} />
              <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 12 }} allowDecimals={false} />
              <Tooltip 
                contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}
              />
              <Area 
                type="monotone" 
                dataKey="count" 
                stroke="hsl(var(--primary))" 
                strokeWidth={3}
                fillOpacity={1} 
                fill="url(#colorCount)" 
                animationDuration={1000}
              />
            </AreaChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </div>
  );
}
