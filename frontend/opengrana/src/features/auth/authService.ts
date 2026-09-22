import { api } from "@/services/api";
import type { LoginRequest, RegisterRequest, User } from "./types";

export const authService = {
  login: (data: LoginRequest) => api.post<void>("/auth/login", data),
  logout: () => api.post<void>("/auth/logout", {}),
  register: (data: RegisterRequest) => api.post<User>("/users", data),
  getCurrentUser: () => api.get<User>("/users/me"),
};
