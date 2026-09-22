import { useState, useEffect, type ReactNode } from 'react'
import { authService } from './authService'
import { AuthContext } from './AuthContext'
import type { LoginRequest, User } from './types'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    let isCurrent = true

    authService
      .getCurrentUser()
      .then((fetchedUser) => {
        if (isCurrent) setUser(fetchedUser)
      })
      .catch(() => {
        if (isCurrent) setUser(null)
      })
      .finally(() => {
        if (isCurrent) setIsLoading(false)
      })

    return () => {
      isCurrent = false
    }
  }, [])

  async function login(data: LoginRequest) {
    setIsLoading(true)

    try {
      await authService.login(data)
      const fetchedUser = await authService.getCurrentUser()
      setUser(fetchedUser)
    } finally {
      setIsLoading(false)
    }
  }

  async function logout() {
    await authService.logout()
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated: !!user, isLoading, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  )
}