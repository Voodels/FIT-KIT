"use client";

import React, { ReactNode } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Dumbbell, History, Trophy, Plus, Settings, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { useUserStore } from "@/store/user-store";

interface LayoutProps {
  children: ReactNode;
}

const NAV_ITEMS = [
  { label: "Journal", href: "/", icon: History },
  { label: "Progress", href: "/progress", icon: Trophy },
];

export function AppLayout({ children }: LayoutProps) {
  const pathname = usePathname();
  const username = useUserStore((state) => state.username);

  return (
    <div className="flex min-h-screen bg-[#F8F9FA] dark:bg-zinc-950 text-foreground selection:bg-primary/10">
      {/* Sidebar - Desktop */}
      <aside className="hidden md:flex flex-col w-64 border-r bg-white dark:bg-zinc-900 p-6 sticky top-0 h-screen z-20">
        <div className="flex items-center gap-3 mb-12 px-2">
          <div className="bg-primary text-primary-foreground p-2 rounded-xl shadow-lg shadow-primary/20">
            <Dumbbell className="w-6 h-6" />
          </div>
          <span className="text-xl font-black tracking-tighter uppercase italic">FITKIT</span>
        </div>

        <nav className="flex-1 space-y-1">
          {NAV_ITEMS.map((item) => (
            <Link key={item.href} href={item.href}>
              <Button 
                variant="ghost" 
                className={cn(
                  "w-full justify-start gap-4 rounded-xl h-12 font-medium transition-all",
                  pathname === item.href 
                    ? "bg-primary/10 text-primary hover:bg-primary/20" 
                    : "text-muted-foreground hover:text-foreground hover:bg-muted"
                )}
              >
                <item.icon className={cn("w-5 h-5", pathname === item.href && "text-primary")} />
                {item.label}
              </Button>
            </Link>
          ))}
        </nav>

        <div className="mt-auto space-y-4">
          <Link href="/workouts/new">
            <Button className="w-full gap-2 rounded-xl h-14 text-base font-bold shadow-lg shadow-primary/25 hover:shadow-primary/40 active:scale-95 transition-all">
              <Plus className="w-6 h-6" />
              Log Workout
            </Button>
          </Link>
          
          <div className="flex items-center gap-3 p-3 rounded-2xl bg-muted/50 border border-transparent hover:border-border transition-colors cursor-pointer group">
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
              <User className="w-5 h-5 text-primary" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-bold truncate">{username || "Guest"}</p>
              <p className="text-[10px] text-muted-foreground font-medium uppercase tracking-wider">Pro Member</p>
            </div>
            <Settings className="w-4 h-4 text-muted-foreground group-hover:rotate-45 transition-transform" />
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Mobile Header */}
        <header className="md:hidden sticky top-0 z-30 bg-white/80 dark:bg-zinc-900/80 backdrop-blur-xl border-b px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="bg-primary p-1.5 rounded-lg">
              <Dumbbell className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="text-lg font-black tracking-tighter uppercase italic">FITKIT</span>
          </div>
          <Link href="/workouts/new">
            <Button size="icon" className="rounded-full shadow-lg">
              <Plus className="w-5 h-5" />
            </Button>
          </Link>
        </header>

        <main className="flex-1 w-full relative">
          {children}
        </main>

        {/* Mobile Nav */}
        <nav className="md:hidden fixed bottom-0 left-0 right-0 z-30 bg-white/80 dark:bg-zinc-900/80 backdrop-blur-xl border-t px-8 py-3 flex items-center justify-around">
          {NAV_ITEMS.map((item) => (
            <Link key={item.href} href={item.href} className="flex flex-col items-center gap-1">
              <item.icon className={cn("w-6 h-6", pathname === item.href ? "text-primary" : "text-muted-foreground")} />
              <span className={cn("text-[10px] font-bold uppercase tracking-widest", pathname === item.href ? "text-primary" : "text-muted-foreground")}>
                {item.label}
              </span>
            </Link>
          ))}
        </nav>
      </div>
    </div>
  );
}
