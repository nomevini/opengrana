import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import App from "./App";

describe("App", () => {
  it("exibe a página de login", () => {
    window.history.pushState({}, "", "/login");

    render(<App />);

    expect(
      screen.getByRole("heading", { name: "Entrar na sua conta" }),
    ).toBeInTheDocument();
  });

  it("exibe a página de cadastro", () => {
    window.history.pushState({}, "", "/register");

    render(<App />);

    expect(
      screen.getByRole("heading", { name: "Criar conta" }),
    ).toBeInTheDocument();
  });
});