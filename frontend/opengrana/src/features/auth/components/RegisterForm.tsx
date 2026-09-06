import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Button } from '@/shared/components/ui/button'
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from '@/shared/components/ui/field'
import { Input } from '@/shared/components/ui/input'
import { authService } from '../authService'

export function RegisterForm() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const navigate = useNavigate()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      await authService.register({ name, email, password })
      navigate('/login')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="name">Nome</FieldLabel>
          <Input
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Seu nome"
            required
            minLength={2}
          />
        </Field>

        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="voce@exemplo.com"
            required
          />
        </Field>

        <Field>
          <FieldLabel htmlFor="password">Senha</FieldLabel>
          <Input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            required
            minLength={8}
          />
        </Field>

        {error && (
          <p className="text-center text-sm text-destructive">{error}</p>
        )}

        <Field>
          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Criando conta...' : 'Criar conta'}
          </Button>
        </Field>

        <FieldDescription className="text-center">
          Já tem uma conta?{' '}
          <Link to="/login" className="font-medium text-primary underline underline-offset-4 hover:opacity-80">
            Entrar
          </Link>
        </FieldDescription>
      </FieldGroup>
    </form>
  )
}