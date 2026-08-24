import { ArrowRight } from "lucide-react";

import { Button } from "@/components/ui/button";

function App() {
  return (
    <main className="grid min-h-svh place-items-center bg-muted/40 p-6">
      <section className="w-full max-w-md rounded-xl border bg-background p-8 shadow-sm">
        <p className="text-sm font-medium text-muted-foreground">OpenGrana</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight">
          Base do frontend pronta.
        </h1>
        <p className="mt-3 text-muted-foreground">
          React, Tailwind CSS e shadcn/ui estão configurados para começar.
        </p>
        <Button className="mt-6">
          Começar projeto <ArrowRight />
        </Button>
      </section>
    </main>
  );
}

export default App;
