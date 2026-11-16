import { Link } from 'react-router-dom'
import {
  ArrowRight,
  GalleryVerticalEnd,
  PieChart,
  ShieldCheck,
  TrendingUp,
  Wallet,
} from 'lucide-react'

import { Button } from '@/shared/components/ui/button'
import { ThemeToggle } from '@/shared/components/ThemeToggle'
import { AsciiGlobe } from '../components/AsciiGlobe'

const features = [
  {
    icon: Wallet,
    title: 'Controle de gastos',
    description:
      'Registre receitas e despesas e acompanhe seu saldo em um só lugar, sem planilhas.',
  },
  {
    icon: PieChart,
    title: 'Orçamento por categoria',
    description:
      'Defina limites por categoria e descubra rapidamente para onde seu dinheiro está indo.',
  },
  {
    icon: TrendingUp,
    title: 'Relatórios e evolução',
    description:
      'Visualize sua evolução financeira mês a mês e tome decisões com mais clareza.',
  },
]

export function LandingPage() {
  return (
    <div className="flex min-h-svh flex-col bg-background">
      <header className="sticky top-0 z-50 border-b border-border/60 bg-background/80 backdrop-blur-sm">
        <div className="mx-auto flex h-16 w-full max-w-6xl items-center justify-between gap-4 px-4 md:px-8">
          <Link
            to="/"
            className="flex items-center gap-2 font-medium text-foreground"
          >
            <span className="flex size-8 items-center justify-center rounded-md bg-primary text-primary-foreground shadow-sm">
              <GalleryVerticalEnd className="size-4" />
            </span>
            <span className="text-base font-semibold">OpenGrana</span>
          </Link>

          <div className="flex items-center gap-2">
            <ThemeToggle />
            <Button asChild variant="ghost" className="hidden sm:inline-flex">
              <Link to="/login">Entrar</Link>
            </Button>
            <Button asChild>
              <Link to="/register">Criar conta</Link>
            </Button>
          </div>
        </div>
      </header>

      <main className="flex-1">
        <section className="relative isolate flex min-h-[calc(100svh-4rem)] w-full items-center justify-center overflow-hidden px-4 py-16 md:px-8">
          <div
            aria-hidden="true"
            className="pointer-events-none absolute inset-0 -z-10 flex items-center justify-center"
          >
            <AsciiGlobe className="text-[4.5px] opacity-70 sm:text-[6.5px] md:text-[9px]" />
          </div>
          <div
            aria-hidden="true"
            className="pointer-events-none absolute inset-0 -z-10"
            style={{
              background:
                'radial-gradient(ellipse 64% 54% at 50% 50%, var(--background) 40%, transparent 88%)',
            }}
          />

          <div className="relative flex flex-col items-center gap-6 text-center">
            <span className="inline-flex items-center gap-2 rounded-full border border-border bg-muted/60 px-3 py-1 text-xs font-medium text-muted-foreground">
              <ShieldCheck className="size-3.5" />
              Suas finanças, sob seu controle
            </span>

            <h1 className="max-w-3xl text-4xl font-bold tracking-tight text-balance sm:text-5xl md:text-6xl">
              Organize seu dinheiro de forma{' '}
              <span className="text-primary">simples e aberta</span>
            </h1>

            <p className="max-w-2xl text-base text-pretty text-muted-foreground sm:text-lg">
              O OpenGrana reúne suas receitas, despesas e orçamentos em um painel
              claro, para você entender sua vida financeira sem complicação.
            </p>

            <div className="flex flex-col gap-3 sm:flex-row">
              <Button asChild size="lg">
                <Link to="/login">
                  Acessar sua conta
                  <ArrowRight className="size-4" />
                </Link>
              </Button>
              <Button asChild size="lg" variant="outline">
                <Link to="/register">Criar conta grátis</Link>
              </Button>
            </div>
          </div>
        </section>

        <section className="border-t border-border/60 bg-muted/30">
          <div className="mx-auto w-full max-w-6xl px-4 py-16 md:px-8 md:py-24">
            <div className="mx-auto mb-12 max-w-2xl text-center">
              <h2 className="text-2xl font-semibold tracking-tight sm:text-3xl">
                Tudo o que você precisa para começar
              </h2>
              <p className="mt-3 text-muted-foreground">
                Uma base sólida para acompanhar suas finanças do dia a dia.
              </p>
            </div>

            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {features.map(({ icon: Icon, title, description }) => (
                <div
                  key={title}
                  className="flex flex-col gap-4 rounded-xl border border-border bg-card p-6 shadow-sm ring-1 ring-border/60"
                >
                  <span className="flex size-10 items-center justify-center rounded-md bg-accent text-accent-foreground">
                    <Icon className="size-5" />
                  </span>
                  <div className="flex flex-col gap-1.5">
                    <h3 className="font-semibold">{title}</h3>
                    <p className="text-sm text-muted-foreground">
                      {description}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="mx-auto w-full max-w-6xl px-4 py-20 md:px-8">
          <div className="flex flex-col items-center gap-6 rounded-2xl border border-border bg-card p-8 text-center shadow-sm ring-1 ring-border/60 md:p-12">
            <h2 className="max-w-2xl text-2xl font-semibold tracking-tight text-balance sm:text-3xl">
              Pronto para colocar suas contas em ordem?
            </h2>
            <p className="max-w-xl text-muted-foreground">
              Entre na sua conta ou crie uma agora mesmo e comece a acompanhar sua
              vida financeira.
            </p>
            <Button asChild size="lg">
              <Link to="/login">
                Entrar
                <ArrowRight className="size-4" />
              </Link>
            </Button>
          </div>
        </section>
      </main>

      <footer className="border-t border-border/60">
        <div className="mx-auto flex w-full max-w-6xl flex-col items-center justify-between gap-4 px-4 py-8 text-sm text-muted-foreground md:flex-row md:px-8">
          <div className="flex items-center gap-2">
            <span className="flex size-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <GalleryVerticalEnd className="size-3.5" />
            </span>
            <span>OpenGrana</span>
          </div>
          <p>Organize suas finanças de forma simples e aberta.</p>
        </div>
      </footer>
    </div>
  )
}
