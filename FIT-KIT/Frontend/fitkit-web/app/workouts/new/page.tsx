"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { MuscleMap } from "@/components/muscle-map";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { workoutApi, mediaApi, uploadValidation } from "@/lib/api";
import { toast } from "sonner";
import { Loader2, Camera, Upload, ArrowLeft } from "lucide-react";
import Link from "next/link";
import { ErrorModal } from "@/components/error-modal";
import { useUserStore } from "@/store/user-store";

export default function NewWorkoutPage() {
  const router = useRouter();
  const [caption, setCaption] = useState("");
  const [selectedMuscles, setSelectedMuscles] = useState<string[]>([]);
  const [file, setFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  
  const { userId } = useUserStore();

  // State for the Error Modal
  const [errorModal, setErrorModal] = useState({
    isOpen: false,
    title: "",
    message: ""
  });

  useEffect(() => {
    if (!userId) {
      router.replace("/auth");
    }
  }, [userId, router]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      if (!uploadValidation.allowedContentTypes.has(selectedFile.type)) {
        toast.error("Only JPG, PNG, or WebP files are allowed.");
        return;
      }
      if (selectedFile.size > uploadValidation.maxBytes) {
        toast.error("File is too large. Max 5MB.");
        return;
      }
      setFile(selectedFile);
      const url = URL.createObjectURL(selectedFile);
      setPreviewUrl(url);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      toast.error("Show off that pump! Please upload a photo.");
      return;
    }
    if (selectedMuscles.length === 0) {
      toast.error("What did you train? Select at least one muscle.");
      return;
    }

    if (!userId) {
      toast.error("Please log in to continue.");
      router.push("/auth");
      return;
    }

    setIsSubmitting(true);
    setUploadProgress(0);
    try {
      // 1. Direct Multipart Upload
      const { fileUrl } = await mediaApi.uploadFile(file, setUploadProgress);

      // 2. Create workout log
      await workoutApi.createWorkout({
        photoUrl: fileUrl,
        caption,
        musclesTargeted: selectedMuscles,
      });

      toast.success("Workout logged! Keep up the momentum.");
      router.push("/");
    } catch (error: any) {
      console.error(error);
      
      // Check for specific backend validation messages (like the 500-character limit)
      const backendError = error.response?.data?.message || "Something went wrong. Let's try that again.";
      
      setErrorModal({
        isOpen: true,
        title: "Workout Failed",
        message: backendError
      });
    } finally {
      setIsSubmitting(false);
      setUploadProgress(0);
    }
  };

  return (
    <div className="container max-w-5xl mx-auto py-12 px-6 pb-24">
      <ErrorModal 
        isOpen={errorModal.isOpen} 
        onClose={() => setErrorModal(prev => ({ ...prev, isOpen: false }))}
        title={errorModal.title}
        message={errorModal.message}
      />

      <div className="mb-10 flex items-center justify-between">
        <div className="flex flex-col gap-2">
          <Link href="/" className="flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors mb-2 text-sm font-bold uppercase tracking-widest">
            <ArrowLeft className="w-4 h-4" />
            Back to Journal
          </Link>
          <h1 className="text-4xl font-black tracking-tighter uppercase italic">Log Workout</h1>
        </div>
      </div>
      
      <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-start">
        <div className="space-y-8">
          <Card className="border-none shadow-md overflow-hidden">
            <CardHeader className="bg-muted/30">
              <CardTitle>Workout Visuals</CardTitle>
              <CardDescription>Upload a progress photo or a gym snap</CardDescription>
            </CardHeader>
            <CardContent className="pt-6 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="caption" className="font-bold uppercase tracking-widest text-[10px]">Caption</Label>
                <Textarea
                  id="caption"
                  placeholder="How did it feel? Any new PRs?"
                  value={caption}
                  onChange={(e) => setCaption(e.target.value)}
                  className="resize-none min-h-[100px] bg-muted/30 border-none focus-visible:ring-primary/20 rounded-2xl p-4"
                />
              </div>

              <div className="space-y-3">
                <Label className="font-bold uppercase tracking-widest text-[10px]">Progress Photo</Label>
                <div 
                  className="border-2 border-dashed rounded-3xl p-4 flex flex-col items-center justify-center cursor-pointer hover:bg-muted/50 transition-all aspect-video bg-muted/20 overflow-hidden relative group"
                  onClick={() => document.getElementById("file-upload")?.click()}
                >
                  {previewUrl ? (
                    <img src={previewUrl} alt="Preview" className="w-full h-full object-cover rounded-2xl" />
                  ) : (
                    <div className="flex flex-col items-center gap-3">
                      <div className="p-4 bg-white dark:bg-zinc-800 rounded-2xl shadow-sm group-hover:scale-110 transition-transform">
                        <Camera className="w-8 h-8 text-primary" />
                      </div>
                      <p className="text-sm text-muted-foreground font-medium">
                        Tap to upload your progress
                      </p>
                    </div>
                  )}
                  <input
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleFileChange}
                  />
                </div>
                <p className="text-xs text-muted-foreground">
                  Max 5MB, JPG/PNG/WebP
                </p>
                {file && (
                  <p className="text-xs text-muted-foreground">
                    Selected: {file.name} ({formatBytes(file.size)})
                  </p>
                )}
                {isSubmitting && file && (
                  <p className="text-xs text-primary font-semibold">
                    Uploading... {uploadProgress}%
                  </p>
                )}
              </div>
            </CardContent>
          </Card>

          <Button 
            type="submit" 
            className="w-full h-16 text-lg font-black uppercase italic tracking-tighter shadow-xl shadow-primary/20 hover:shadow-primary/30 transition-all active:scale-95" 
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <>
                <Loader2 className="mr-3 h-5 w-5 animate-spin" />
                Logging...
              </>
            ) : (
              <>
                <Upload className="mr-3 h-5 w-5" />
                Finish Session
              </>
            )}
          </Button>
        </div>

        <div className="space-y-8">
          <Card className="border-none shadow-md overflow-hidden">
            <CardHeader className="bg-muted/30">
              <CardTitle>Muscle Selection</CardTitle>
              <CardDescription>Select the target areas of your session</CardDescription>
            </CardHeader>
            <CardContent className="pt-6">
              <MuscleMap 
                selectedMuscles={selectedMuscles} 
                onMusclesChange={setSelectedMuscles} 
              />
            </CardContent>
          </Card>
        </div>
      </form>
    </div>
  );
}

function formatBytes(bytes: number) {
  if (bytes <= 0) return "0 B";
  const units = ["B", "KB", "MB", "GB"];
  const index = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
  const value = bytes / Math.pow(1024, index);
  return `${value.toFixed(1)} ${units[index]}`;
}
