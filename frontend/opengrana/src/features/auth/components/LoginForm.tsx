import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { cn } from '@/shared/lib/utils'
import { Button } from '@/shared/components/ui/button'
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from '@/shared/components/ui/field'
import { Input } from '@/shared/components/ui/input'
import { useAuth } from '../useAuth'

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<'form'>) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      await login({ email, password })
      navigate('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className={cn('flex flex-col gap-6', className)} {...props}>
      <FieldGroup>
        <div className="flex flex-col items-center gap-1 text-center">
          <h1 className="text-2xl font-bold tracking-tight">Entrar na sua conta</h1>
          <p className="text-sm text-balance text-muted-foreground">
            Digite seu email abaixo para acessar seu painel financeiro
          </p>
        </div>

        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input
            id="email"
            type="email"
            placeholder="voce@exemplo.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </Field>

        <Field>
          <FieldLabel htmlFor="password">Senha</FieldLabel>
          <Input
            id="password"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </Field>

        {error && (
          <p className="text-sm text-destructive text-center">{error}</p>
        )}

        <Field>
          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </Button>
        </Field>

        <FieldDescription className="text-center">
          Não tem uma conta?{' '}
          <Link to="/register" className="font-medium text-primary underline underline-offset-4 hover:opacity-80">
            Criar conta
          </Link>
        </FieldDescription>
      </FieldGroup>
    </form>
  )
}