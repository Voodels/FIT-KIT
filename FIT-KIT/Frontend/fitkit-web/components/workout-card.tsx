"use client";

import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { formatDistanceToNow } from "date-fns";
import { Dumbbell, Calendar } from "lucide-react";
import Image from "next/image";
import { useEffect, useMemo, useState } from "react";
import { mediaApi } from "@/lib/api";

interface WorkoutCardProps {
  workout: {
    id: string;
    photoUrl: string;
    caption: string;
    musclesTargeted: string[];
    loggedAt: string;
  };
}

export function WorkoutCard({ workout }: WorkoutCardProps) {
  const date = workout.loggedAt ? new Date(workout.loggedAt) : new Date();
  const isValidDate = !isNaN(date.getTime());
  const [displayUrl, setDisplayUrl] = useState<string>(workout.photoUrl);

  const isS3Url = useMemo(() => workout.photoUrl.includes("amazonaws.com"), [workout.photoUrl]);

  useEffect(() => {
    const loadPresignedUrl = async () => {
      if (!isS3Url) {
        setDisplayUrl(workout.photoUrl);
        return;
      }

      const objectKey = extractObjectKey(workout.photoUrl);
      if (!objectKey) {
        setDisplayUrl(workout.photoUrl);
        return;
      }

      try {
        const response = await mediaApi.presignDownload(objectKey);
        setDisplayUrl(response.downloadUrl);
      } catch (error) {
        setDisplayUrl(workout.photoUrl);
      }
    };

    loadPresignedUrl();
  }, [workout.photoUrl, isS3Url]);

  return (
    <Card className="overflow-hidden bg-card border-none shadow-sm hover:shadow-md transition-shadow">
      <CardHeader className="p-4 flex flex-row items-center gap-3">
        <Avatar className="h-10 w-10">
          <AvatarImage src="https://github.com/shadcn.png" />
          <AvatarFallback>UN</AvatarFallback>
        </Avatar>
        <div className="flex flex-col">
          <span className="font-semibold text-sm">User Name</span>
          <span className="text-xs text-muted-foreground flex items-center gap-1">
            <Calendar className="w-3 h-3" />
            {isValidDate 
              ? formatDistanceToNow(date, { addSuffix: true })
              : "Just now"}
          </span>
        </div>
      </CardHeader>
      <div className="aspect-square relative overflow-hidden bg-muted">
        <Image 
          src={displayUrl} 
          alt="Workout" 
          fill
          className="object-cover transition-transform hover:scale-105 duration-500"
          unoptimized={displayUrl.startsWith("http")}
        />
      </div>
      <CardContent className="p-4 space-y-3">
        {workout.caption && (
          <p className="text-sm text-foreground leading-relaxed">
            {workout.caption}
          </p>
        )}
        <div className="flex flex-wrap gap-1.5">
          {workout.musclesTargeted.map((muscle) => (
            <Badge key={muscle} variant="secondary" className="bg-primary/5 hover:bg-primary/10 text-primary border-none text-[10px] uppercase font-bold tracking-wider">
              <Dumbbell className="w-3 h-3 mr-1" />
              {muscle}
            </Badge>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}

function extractObjectKey(url: string) {
  const match = url.match(/amazonaws\.com\/(.+)$/);
  return match ? match[1] : null;
}
