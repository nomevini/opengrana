import { api } from "@/services/api";
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  User,
} from "./types";

export const authService = {
  login: (data: LoginRequest) => api.post<LoginResponse>("/auth/login", data),
  register: (data: RegisterRequest) => api.post<User>("/users", data),
  getCurrentUser: (token: string) =>
    api.get<User>("/users/me", {
      headers: { Authorization: `Bearer ${token}` },
    }),
};
