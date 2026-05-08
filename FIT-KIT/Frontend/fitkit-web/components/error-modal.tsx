"use client";

import React from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { AlertCircle, X } from "lucide-react";

interface ErrorModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  message: string;
}

export function ErrorModal({ isOpen, onClose, title = "Action Required", message }: ErrorModalProps) {
  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-[400px] border-none bg-white/80 dark:bg-zinc-900/80 backdrop-blur-2xl shadow-2xl rounded-[32px] overflow-hidden p-0 gap-0">
        <div className="bg-destructive/10 p-8 flex flex-col items-center justify-center text-center gap-4">
          <div className="w-16 h-16 bg-destructive/20 rounded-full flex items-center justify-center animate-bounce">
            <AlertCircle className="w-8 h-8 text-destructive" />
          </div>
          <DialogHeader className="p-0 space-y-1">
            <DialogTitle className="text-2xl font-black tracking-tighter uppercase italic text-destructive">
              {title}
            </DialogTitle>
            <DialogDescription className="text-muted-foreground font-medium">
              We couldn&apos;t complete that request. Check the details below.
            </DialogDescription>
          </DialogHeader>
        </div>
        
        <div className="p-8">
          <p className="text-center text-sm font-bold leading-relaxed text-foreground/80">
            {message}
          </p>
        </div>

        <DialogFooter className="p-6 pt-0 sm:justify-center">
          <Button 
            onClick={onClose}
            className="w-full h-14 rounded-2xl font-black uppercase italic tracking-tighter shadow-lg shadow-destructive/20 hover:bg-destructive hover:text-white transition-all active:scale-95"
            variant="secondary"
          >
            Got it, I&apos;ll fix it
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
