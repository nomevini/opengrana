import { GalleryVerticalEnd } from 'lucide-react'
import { ThemeToggle } from '@/shared/components/ThemeToggle'
import { LoginForm } from '../components/LoginForm'

export function LoginPage() {
  return (
    <div className="relative flex min-h-svh items-center justify-center bg-muted/60 p-4 md:p-8">
      <div className="absolute right-4 top-4 md:right-6 md:top-6">
        <ThemeToggle />
      </div>

      <div className="w-full max-w-md rounded-xl border border-border bg-card p-6 shadow-sm ring-1 ring-border/60 sm:p-8">
        <div className="flex flex-col gap-6">
          <a href="#" className="flex items-center justify-center gap-2 font-medium text-foreground">
            <div className="flex size-8 items-center justify-center rounded-md bg-primary text-primary-foreground shadow-sm">
              <GalleryVerticalEnd className="size-4" />
            </div>
            <span className="text-base">OpenGrana</span>
          </a>

          <LoginForm className="mt-0" />
        </div>
      </div>
    </div>
  )
}