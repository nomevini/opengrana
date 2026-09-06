import { GalleryVerticalEnd } from 'lucide-react'
import { ThemeToggle } from '@/shared/components/ThemeToggle'
import { RegisterForm } from '@/features/auth/components/RegisterForm'

export function RegisterPage() {
  return (
    <div className="relative flex min-h-svh items-center justify-center bg-muted/60 p-4 md:p-8">
      <div className="absolute right-4 top-4 md:right-6 md:top-6">
        <ThemeToggle />
      </div>

      <div className="w-full max-w-md rounded-xl border border-border bg-card p-6 shadow-sm ring-1 ring-border/60 sm:p-8">
        <div className="mb-6 flex flex-col items-center gap-3 text-center">
          <div className="flex size-10 items-center justify-center rounded-md bg-primary text-primary-foreground shadow-sm">
            <GalleryVerticalEnd className="size-5" />
          </div>
          <div>
            <p className="text-sm font-medium text-muted-foreground">OpenGrana</p>
            <h1 className="text-2xl font-semibold tracking-tight">Criar conta</h1>
          </div>
        </div>

        <RegisterForm />
      </div>
    </div>
  )
}