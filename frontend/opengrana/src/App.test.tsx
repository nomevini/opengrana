import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import App from "./App";

describe("App", () => {
  it("exibe a tela inicial do OpenGrana", () => {
    render(<App />);

    expect(
      screen.getByRole("heading", { name: "Base do frontend pronta." }),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /começar projeto/i })).toBeInTheDocument();
  });
});
