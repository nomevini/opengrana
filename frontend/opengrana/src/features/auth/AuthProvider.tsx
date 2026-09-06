import { useState, useEffect, type ReactNode } from 'react'
import { authService } from './authService'
import { AuthContext } from './AuthContext'
import type { LoginRequest, User } from './types'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem('token')
  )
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState<boolean>(
    () => !!localStorage.getItem('token')
  )

  useEffect(() => {
    if (!token) {
      return
    }

    let isCurrent = true

    authService
      .getCurrentUser(token)
      .then((fetchedUser) => {
        if (isCurrent) setUser(fetchedUser)
      })
      .catch(() => {
        if (isCurrent) {
          setToken(null)
          localStorage.removeItem('token')
        }
      })
      .finally(() => {
        if (isCurrent) setIsLoading(false)
      })

    return () => {
      isCurrent = false
    }
  }, [token])

  async function login(data: LoginRequest) {
    setIsLoading(true)
    const response = await authService.login(data)
    setToken(response.token)
    localStorage.setItem('token', response.token)
  }

  function logout() {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
  }

  return (
    <AuthContext.Provider
      value={{ user, token, isAuthenticated: !!token, isLoading, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  )
}