import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8082/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

const MAX_UPLOAD_BYTES = 5 * 1024 * 1024;
const ALLOWED_CONTENT_TYPES = new Set(["image/jpeg", "image/png", "image/webp"]);

type PresignedCacheEntry = {
  url: string;
  expiresAt: number;
};

const presignedDownloadCache = new Map<string, PresignedCacheEntry>();

export const uploadValidation = {
  maxBytes: MAX_UPLOAD_BYTES,
  allowedContentTypes: ALLOWED_CONTENT_TYPES,
};

export const workoutApi = {
  createWorkout: async (data: { photoUrl: string; caption: string; musclesTargeted: string[]; sets?: any[] }) => {
    try {
      const response = await api.post("/workouts", data);
      return response.data;
    } catch (error) {
      console.error("Backend error during workout creation:", error);
      throw error;
    }
  },
  
  getWorkouts: async (page = 0, size = 10) => {
    try {
      const response = await api.get(`/workouts?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        return {
          unauthorized: true,
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: 10,
          number: 0,
        };
      }
      console.warn("Backend not reachable, returning empty feed", error);
      return {
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 10,
        number: 0,
      };
    }
  }
};

export const mediaApi = {
  presignUpload: async (file: File) => {
    const response = await api.post("/media/presign/upload", {
      fileName: file.name,
      contentType: file.type,
      fileSize: file.size,
    });
    return response.data as { uploadUrl: string; objectKey: string; fileUrl: string };
  },
  presignDownload: async (objectKey: string) => {
    const cached = presignedDownloadCache.get(objectKey);
    const now = Date.now();
    if (cached && cached.expiresAt > now) {
      return { downloadUrl: cached.url, objectKey };
    }

    const response = await api.get("/media/presign/download", {
      params: { objectKey },
    });
    const payload = response.data as { downloadUrl: string; objectKey: string };
    presignedDownloadCache.set(objectKey, {
      url: payload.downloadUrl,
      expiresAt: now + 9 * 60 * 1000,
    });
    return payload;
  },
  uploadFile: async (file: File, onProgress?: (percent: number) => void) => {
    const formData = new FormData();
    formData.append("file", file);

    try {
      try {
        const presign = await mediaApi.presignUpload(file);
        await axios.put(presign.uploadUrl, file, {
          headers: {
            "Content-Type": file.type,
          },
          onUploadProgress: (event) => {
            if (!event.total || !onProgress) return;
            const percent = Math.round((event.loaded / event.total) * 100);
            onProgress(percent);
          },
        });
        return { fileUrl: presign.fileUrl, objectKey: presign.objectKey };
      } catch (presignError) {
        const response = await api.post("/media/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        return response.data; // { fileUrl: "..." }
      }
    } catch (error) {
      console.error("Upload failed:", error);
      throw error;
    }
  }
};

export const userApi = {
  register: async (username: string, email: string, password: string) => {
    console.log("=== FRONTEND REGISTRATION DEBUG ===");
    console.log("Sending registration request with:");
    console.log("Username:", username, "(length:", username.length, ")");
    console.log("Email:", email);
    console.log("Password:", password, "(length:", password.length, ")");
    console.log("API Base URL:", api.defaults.baseURL);
    console.log("===================================");
    
    try {
      const requestData = {
        username,
        email,
        password,
      };
      console.log("Request payload:", JSON.stringify(requestData, null, 2));
      
      const response = await api.post("/users/register", requestData);
      
      console.log("=== REGISTRATION SUCCESS ===");
      console.log("Response status:", response.status);
      console.log("Response data:", response.data);
      console.log("=============================");
      
      return {
        id: response.data.id,
        username: response.data.username,
        email,
      } as { id: string; username: string; email: string };
    } catch (error: any) {
      console.error("=== REGISTRATION ERROR ===");
      console.error("Error details:", error);
      if (error.response) {
        console.error("Response status:", error.response.status);
        console.error("Response data:", error.response.data);
        console.error("Response headers:", error.response.headers);
      }
      console.error("==========================");
      throw error;
    }
  }
};

export const authApi = {
  login: async (login: string, password: string) => {
    const response = await api.post("/auth/login", {
      login,
      password,
    });
    return response.data as { userId: string; username: string; profilePicUrl: string | null };
  },
  logout: async () => {
    await api.post("/auth/logout");
  },
};
